package net.ctd.ctdmod;

import com.mojang.blaze3d.platform.InputConstants;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.animation.PlayerRawAnimationBuilder;
import com.zigythebird.playeranimcore.animation.RawAnimation;
import com.zigythebird.playeranimcore.enums.PlayState;
import net.ctd.ctdmod.client.CTDClientState;
import net.ctd.ctdmod.network.MeditatePayload;
import net.ctd.ctdmod.ui.QiHudOverlay;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Client-only mod initializer. Loaded only on the physical client; safe to use client-side APIs.
 *
 * Registers the config screen, client lifecycle handlers, keybindings, and HUD rendering.
 */
@Mod(value = CTDMod.MODID, dist = Dist.CLIENT)
public class CTDClientMod {

    private static final QiHudOverlay QI_HUD = new QiHudOverlay();

    /** Touche de méditation (personnalisable dans Contrôles). */
    public static final KeyMapping MEDITATE_KEY = new KeyMapping(
        "key.ctdmod.meditate",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_M,
        "key.categories.ctdmod"
    );

    /** Compteur pour limiter le spawn de particules (toutes les 10 ticks). */
    private int meditationParticleTicks;

    /** Layer PAL pour l'animation de méditation. */
    private static final ResourceLocation MEDITATION_LAYER =
        ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "meditation");

    /** Animation de méditation assise (cache statique). */
    private static final ResourceLocation MEDITATION_SIT_ANIM =
        ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "meditation_sit");

    /** RawAnimation cache (préconisé par PAL pour éviter allocations par frame). */
    private static RawAnimation meditationSitRaw;

    public CTDClientMod(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        container.getEventBus().addListener(this::registerKeyMappings);
        container.getEventBus().addListener(this::onClientSetup);
        NeoForge.EVENT_BUS.register(this);
    }

    /**
     * Enregistre le layer d'animation PAL pour la méditation.
     * Obligatoire : utiliser enqueueWork pour NeoForge.
     *
     * Note: forceAnimationReset() est nécessaire car PAL ne redémarre pas une animation
     * identique après PlayState.STOP — il considère qu'elle est déjà chargée.
     * RawAnimation créé à la demande (après le reload des ressources).
     */
    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                MEDITATION_LAYER,
                1000,
                player -> new PlayerAnimationController(player,
                    (controller, state, animSetter) -> {
                        if (CTDClientState.isMeditating()) {
                            if (controller.hasAnimationFinished()) {
                                controller.forceAnimationReset();
                            }
                            if (meditationSitRaw == null) {
                                meditationSitRaw = PlayerRawAnimationBuilder.begin().thenLoop(MEDITATION_SIT_ANIM).build();
                            }
                            return animSetter.setAnimation(meditationSitRaw);
                        }
                        return PlayState.STOP;
                    })
            );
        });
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(MEDITATE_KEY);
    }

    /**
     * Abaisse visuellement le joueur local en méditation pour qu'il paraisse assis au sol.
     */
    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (player == Minecraft.getInstance().player && CTDClientState.isMeditating()) {
            event.getPoseStack().translate(0, -0.65, 0);
        }
    }

    /**
     * Rendu du HUD de Qi à chaque frame de l'overlay GUI.
     *
     * @param event évènement de rendu GUI
     */
    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui || mc.isPaused()) {
            return;
        }

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        QI_HUD.render(event.getGuiGraphics(), width, height, mc.player);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        boolean keyDown = MEDITATE_KEY.isDown();
        if (keyDown != CTDClientState.getLastMeditateKeyState()) {
            CTDClientState.setLastMeditateKeyState(keyDown);
            CTDClientState.setMeditating(keyDown);
            PacketDistributor.sendToServer(new MeditatePayload(keyDown));
        }

        boolean meditating = CTDClientState.isMeditating();
        if (meditating && mc.level != null) {
            if (++meditationParticleTicks >= 10) {
                meditationParticleTicks = 0;
                spawnMeditationParticles(player, mc.level);
            }
        } else {
            meditationParticleTicks = 0;
        }
    }

    /**
     * Spawn des particules (enchant, soul) autour du joueur en méditation.
     */
    private void spawnMeditationParticles(Player player, Level level) {
        double x = player.getX();
        double y = player.getY() + player.getBbHeight() * 0.5;
        double z = player.getZ();
        var random = player.getRandom();
        for (int i = 0; i < 3; i++) {
            double ox = (random.nextDouble() - 0.5) * 0.8;
            double oz = (random.nextDouble() - 0.5) * 0.8;
            level.addParticle(ParticleTypes.ENCHANT, x + ox, y, z + oz, 0.0, 0.02, 0.0);
            level.addParticle(ParticleTypes.SOUL, x - ox * 0.5, y + 0.2, z - oz * 0.5, 0.0, 0.01, 0.0);
        }
    }
}
