package net.ctd.ctdmod.core.definition;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Preconditions;
import net.ctd.ctdmod.CTDMod;
import net.ctd.ctdmod.api.CTDCreativeTabIds;
import net.ctd.ctdmod.core.MainCreativeTab;
import net.ctd.ctdmod.items.misc.Elbaboss;

import net.ctd.ctdmod.items.weapons.Katana;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * Central registry for all custom items in the mod.
 * <p>
 * Items are registered via {@link #DR} and stored as {@link ItemDefinition}s. Use
 * {@link #item(String, ResourceLocation, Function)} or its overload to register new items.
 */
public class CTDItems {
    public static final DeferredRegister.Items DR = DeferredRegister.createItems(CTDMod.MODID);

    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    // -------------------------------------------------------------------------
    // ITEMS
    // -------------------------------------------------------------------------

    public static final ItemDefinition<Item> ELBABOSS = item(
            "elbaboss",
            ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "elbaboss"),
            properties -> new Elbaboss(properties)
    );

    public static final ItemDefinition<Item> KATANA = item(
            "katana",
            ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "katana"),
            properties -> new Katana(properties),
            CTDCreativeTabIds.ALCHEMY
    );

    // -------------------------------------------------------------------------
    // Registration API
    // -------------------------------------------------------------------------

    /**
     * Returns an unmodifiable list of all registered item definitions.
     *
     * @return the list of {@link ItemDefinition}s
     */
    public static List<ItemDefinition<?>> getItems(){
        return Collections.unmodifiableList(ITEMS);
    }

    /**
     * Registers a new item and adds it to the main creative tab.
     *
     * @param name    The readable English name (for internal and localization use).
     * @param id      The {@link ResourceLocation} identifying the item (must use this mod's namespace).
     * @param factory A factory that creates the item from {@link Item.Properties}.
     * @return A {@link ItemDefinition} containing the registered item.
     * @throws IllegalArgumentException If {@code id} does not belong to this mod's namespace.
     */
    static <T extends Item> ItemDefinition<T> item(String name, ResourceLocation id, Function<Item.Properties, T> factory){
        return item(name, id, factory, CTDCreativeTabIds.MAIN);
    }

    /**
     * Registers a new item and optionally assigns it to a creative tab.
     *
     * @param name    The readable English name (for internal and localization use).
     * @param id      The {@link ResourceLocation} identifying the item (must use this mod's namespace).
     * @param factory A factory that creates the item from {@link Item.Properties}.
     * @param group   The creative tab for this item, or {@code null} for none. Use {@link CTDCreativeTabIds#MAIN} or {@link CTDCreativeTabIds#ALCHEMY}.
     * @return A {@link ItemDefinition} containing the registered item.
     * @throws IllegalArgumentException If {@code id} does not belong to this mod's namespace.
     */
    static <T extends Item> ItemDefinition<T> item(String name, ResourceLocation id,
                                                   Function<Item.Properties, T> factory,
                                                   @Nullable ResourceKey<CreativeModeTab> group){

        Preconditions.checkArgument(id.getNamespace().equals(CTDMod.MODID), "Namespace mismatch!");

        var deferredItem = DR.registerItem(id.getPath(), factory);

        var definition = new ItemDefinition<>(name, deferredItem);

        if (Objects.equals(group, CTDCreativeTabIds.MAIN)) {
            MainCreativeTab.add(definition);
        } else if (group != null) {
            MainCreativeTab.addExternal(group, definition);
        }

        ITEMS.add(definition);
        return definition;
    }
}
