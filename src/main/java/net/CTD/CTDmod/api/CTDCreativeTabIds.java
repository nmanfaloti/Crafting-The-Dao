package net.CTD.CTDmod.api;

import net.CTD.CTDmod.CTDMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

public class CTDCreativeTabIds {
    private CTDCreativeTabIds(){
    }

    public static final ResourceKey<CreativeModeTab> MAIN = create("main");

    public static final ResourceKey<CreativeModeTab> ALCHEMY = create("alchemy");

    private static ResourceKey<CreativeModeTab> create(String path){
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, path));
    }
}
