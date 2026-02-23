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

/**
 * Registers and populates the mod's creative mode tabs (main tab and alchemy tab).
 * Items are collected via {@link #add(ItemDefinition)} and {@link #addExternal(ResourceKey, ItemDefinition)}
 * during block/item registration, then displayed in {@link #buildDisplayItems}.
 */
public class MainCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "ctdmod");

    public static final ResourceKey<CreativeModeTab> MAIN_TAB_KEY = CTDCreativeTabIds.MAIN;
    public static final ResourceKey<CreativeModeTab> ALCHEMY_TAB_KEY = CTDCreativeTabIds.ALCHEMY;

    /** Items to show in the main CTD creative tab. */
    public static final List<ItemDefinition<?>> itemDefs = new ArrayList<>();

    /** Items to show in external tabs (e.g. alchemy tab), keyed by tab. */
    public static final Multimap<ResourceKey<CreativeModeTab>, ItemDefinition<?>> externalItemDefs =
            HashMultimap.create();

    /** Main CTD creative tab. */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creative_tab.ctd_tab"))
                    .icon(() -> CTDItems.ELBABOSS.stack(1))
                    .displayItems((params, output) -> MainCreativeTab.buildDisplayItems(MAIN_TAB_KEY, output))
                    .build()
    );

    /** Alchemy creative tab. */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ALCHEMY_TAB = CREATIVE_TABS.register("alchemy",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creative_tab.alchemy_tab"))
                    .icon(() -> CTDBlocks.ALCHEMY_CAULDRON.stack(1))
                    .displayItems((params, output) -> MainCreativeTab.buildDisplayItems(ALCHEMY_TAB_KEY, output))
                    .build()
    );

    /**
     * Adds an item to the main creative tab.
     *
     * @param itemDef the item definition to add
     */
    public static void add(ItemDefinition<?> itemDef) {
        itemDefs.add(itemDef);
    }

    /**
     * Adds an item to an external tab (e.g. alchemy).
     *
     * @param tab     the creative tab key
     * @param itemDef the item definition to add
     */
    public static void addExternal(ResourceKey<CreativeModeTab> tab, ItemDefinition<?> itemDef) {
        externalItemDefs.put(tab, itemDef);
    }

    /**
     * Fills the given tab output with the items registered for that tab.
     *
     * @param currentTab the tab being built
     * @param output     the creative tab output to accept items into
     */
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
