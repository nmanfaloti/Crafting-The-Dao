package net.ctd.ctdmod.api;

import net.ctd.ctdmod.CTDMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

/**
 * Resource keys for the mod's creative mode tabs. Used when registering blocks/items
 * to assign them to a tab (e.g. {@link CTDCreativeTabIds#MAIN} or {@link CTDCreativeTabIds#ALCHEMY}).
 */
public class CTDCreativeTabIds {
    private CTDCreativeTabIds() {
    }

    /** Main CTD creative tab. */
    public static final ResourceKey<CreativeModeTab> MAIN = create("main");

    /** Alchemy creative tab. */
    public static final ResourceKey<CreativeModeTab> ALCHEMY = create("alchemy");

    private static ResourceKey<CreativeModeTab> create(String path) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, path));
    }
}
