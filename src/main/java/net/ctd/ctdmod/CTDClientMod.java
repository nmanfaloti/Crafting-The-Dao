package net.ctd.ctdmod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Client-only mod initializer. Loaded only on the physical client; safe to use client-side APIs.
 * <p>
 * Registers the config screen and client lifecycle handlers via {@link EventBusSubscriber}.
 */
@Mod(value = CTDMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CTDMod.MODID, value = Dist.CLIENT)
public class CTDClientMod {
    public CTDClientMod(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    /**
     * Runs when the client is set up. Use for client-only initialization.
     *
     * @param event the client setup event
     */
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        CTDMod.LOGGER.info("CTD Mod client setup");
    }
}
