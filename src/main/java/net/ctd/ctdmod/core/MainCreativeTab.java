package net.ctd.ctdmod.core;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.ctd.ctdmod.api.CTDCreativeTabIds;
import net.ctd.ctdmod.core.definition.CTDBlocks;
import net.ctd.ctdmod.core.definition.CTDItems;
import net.ctd.ctdmod.core.definition.ItemDefinition;
import net.ctd.ctdmod.items.CTDBaseItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;


public class MainCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "ctdmod");

    public static final ResourceKey<CreativeModeTab> MAIN_TAB_KEY = CTDCreativeTabIds.MAIN;
    public static final ResourceKey<CreativeModeTab> ALCHEMY_TAB_KEY = CTDCreativeTabIds.ALCHEMY;

    // Items for the main tab
    public static final List<ItemDefinition<?>> itemDefs = new ArrayList<>();

    // Items for external tabs (like vanilla ones)
    public static final Multimap<ResourceKey<CreativeModeTab>, ItemDefinition<?>> externalItemDefs =
            HashMultimap.create();

    // Main tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creative_tab.ctd_tab"))
                    .icon(() -> CTDItems.ELBABOSS.stack(1))
                    .displayItems((params, output) -> MainCreativeTab.buildDisplayItems(MAIN_TAB_KEY, output))
                    .build()
    );

    // Alchemy tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ALCHEMY_TAB = CREATIVE_TABS.register("alchemy",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creative_tab.alchemy_tab"))
                    .icon(() -> CTDBlocks.ALCHEMY_CAULDRON.stack(1))
                    .displayItems((params, output) -> MainCreativeTab.buildDisplayItems(ALCHEMY_TAB_KEY, output))
                    .build()
    );

    public static void add(ItemDefinition<?> itemDef) {
        itemDefs.add(itemDef);
    }

    public static void addExternal(ResourceKey<CreativeModeTab> tab, ItemDefinition<?> itemDef) {
        externalItemDefs.put(tab, itemDef);
    }

    public static void buildDisplayItems(ResourceKey<CreativeModeTab> currentTab, CreativeModeTab.Output output) {
        if (currentTab.equals(MAIN_TAB_KEY)) {
            for (var itemDef : itemDefs) {
                var item = itemDef.asItem();

                if (item instanceof CTDBaseItem baseItem) {
                    baseItem.addToMainCreativeTab(null, output);
                } else {
                    output.accept(itemDef);
                }
            }
        } else if (currentTab.equals(ALCHEMY_TAB_KEY)) {
            for (var itemDef : externalItemDefs.get(ALCHEMY_TAB_KEY)) {
                var item = itemDef.asItem();

                if (item instanceof CTDBaseItem baseItem) {
                    baseItem.addToAlchemyCreativeTab(null, output);
                } else {
                    output.accept(itemDef);
                }
            }
        }
    }
}
