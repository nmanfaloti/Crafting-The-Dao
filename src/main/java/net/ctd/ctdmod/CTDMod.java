package net.ctd.ctdmod;


import net.ctd.ctdmod.blockentity.entity.alchemy.AlchemyCauldronRenderer;
import net.ctd.ctdmod.core.MainCreativeTab;
import net.ctd.ctdmod.core.definition.CTDBlockEntities;
import net.ctd.ctdmod.core.definition.CTDBlocks;
import net.ctd.ctdmod.core.definition.CTDDataComponents;
import net.ctd.ctdmod.core.definition.CTDItems;
import net.ctd.ctdmod.core.definition.CTDRecipes;
import net.ctd.ctdmod.data.CTDAttachments;
import net.ctd.ctdmod.data.CultivationUtil;
import net.ctd.ctdmod.meditation.MeditationServerHandler;
import net.ctd.ctdmod.network.SyncMeditationStatePayload;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Main mod class for Crafting The Dao (CTD).
 * <p>
 * Registers blocks and items via {@link CTDBlocks} and {@link CTDItems}, creative tabs via
 * {@link MainCreativeTab}, and subscribes to common setup and server lifecycle events.
 */
@Mod(CTDMod.MODID)
public class CTDMod {
    /** Mod identifier; must match the namespace used in assets and registries. */
    public static final String MODID = "ctdmod";

    public static final Logger LOGGER = LogUtils.getLogger();

    public CTDMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        CTDDataComponents.DR.register(modEventBus);
        CTDItems.DR.register(modEventBus);
        CTDBlocks.DR.register(modEventBus);
        MainCreativeTab.CREATIVE_TABS.register(modEventBus);
        CTDBlockEntities.DR.register(modEventBus);
        CTDAttachments.DR.register(modEventBus);


        NeoForge.EVENT_BUS.register(this);

        // Register client-side event listeners only on the client distribution
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(CTDModClient::registerBER);
        }

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    /**
     * Runs during common mod setup (both physical client and dedicated server).
     *
     * @param event the common setup event
     */
    private void commonSetup(FMLCommonSetupEvent event) {
        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }
        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.get());
        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));

        // Wait for neoforge to setup 
        event.enqueueWork(() -> {
            CTDRecipes.init();
        });
    }

    /**
     * Fired when a server is starting. Use for server-side initialization.
     *
     * @param event the server starting event
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("CTD Mod server starting");
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!MeditationServerHandler.hasAnyMeditators()) {
            return;
        }
        if (event.getEntity() instanceof ServerPlayer serverPlayer && !serverPlayer.level().isClientSide()) {
            MeditationServerHandler.tick(serverPlayer);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && !serverPlayer.level().isClientSide()) {
            CultivationUtil.syncCultivationToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            MeditationServerHandler.remove(event.getEntity());
        }
    }

    /**
     * Interrompt la méditation si le joueur prend des dégâts.
     */
    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer
                && !serverPlayer.level().isClientSide()
                && MeditationServerHandler.isMeditating(serverPlayer)) {
            MeditationServerHandler.setMeditating(serverPlayer, false);
            PacketDistributor.sendToPlayer(serverPlayer, new SyncMeditationStatePayload(false));
        }
    }

    public class CTDModClient {
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Client-side setup code here 
        }

        // Cette méthode sera appelée par le Mod Event Bus
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(CTDBlockEntities.ALCHEMY_CAULDRON.get(), AlchemyCauldronRenderer::new);
        }
    }
}
