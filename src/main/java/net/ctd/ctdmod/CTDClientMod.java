package net.ctd.ctdmod;

import com.mojang.blaze3d.platform.InputConstants;
import net.ctd.ctdmod.client.CTDClientState;
import net.ctd.ctdmod.network.MeditatePayload;
import net.ctd.ctdmod.ui.QiHudOverlay;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
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

    private boolean lastMeditateState;

    public CTDClientMod(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        container.getEventBus().addListener(this::registerKeyMappings);
        NeoForge.EVENT_BUS.register(this);
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(MEDITATE_KEY);
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
        if (mc.player == null) {
            return;
        }
        boolean keyDown = MEDITATE_KEY.isDown();
        if (keyDown != lastMeditateState) {
            lastMeditateState = keyDown;
            PacketDistributor.sendToServer(new MeditatePayload(keyDown));
            CTDClientState.setMeditating(keyDown);
        }
    }
}
