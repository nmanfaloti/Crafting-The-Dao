package net.ctd.ctdmod;

import net.ctd.ctdmod.core.MainCreativeTab;
import net.ctd.ctdmod.core.definition.CTDBlocks;
import net.ctd.ctdmod.core.definition.CTDItems;
import net.ctd.ctdmod.technique.DeferredBehaviorScheduler;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

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

        CTDItems.DR.register(modEventBus);
        CTDBlocks.DR.register(modEventBus);
        MainCreativeTab.CREATIVE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

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
    public void onServerTick(ServerTickEvent.Post event) {
        DeferredBehaviorScheduler.tick();
    }
}
