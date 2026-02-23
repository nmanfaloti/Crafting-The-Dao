package net.CTD.CTDmod.core.definition;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Preconditions;
import net.CTD.CTDmod.CTDMod;
import net.CTD.CTDmod.api.CTDCreativeTabIds;
import net.CTD.CTDmod.core.MainCreativeTab;
import net.CTD.CTDmod.items.misc.Elbaboss;

import net.CTD.CTDmod.items.weapons.Katana;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Function;

import static com.mojang.text2speech.Narrator.LOGGER;

public class CTDItems {
    public static final DeferredRegister.Items DR = DeferredRegister.createItems(CTDMod.MODID);

    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    //
    // ITEMS
    //

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

    //
    //
    //
    public static List<ItemDefinition<?>> getItems(){
        return Collections.unmodifiableList(ITEMS);
    }

    static <T extends Item> ItemDefinition<T> item(String name, ResourceLocation id, Function<Item.Properties, T> factory){
        return item(name, id, factory, CTDCreativeTabIds.MAIN);
    }

    static <T extends Item> ItemDefinition<T> item(String name, ResourceLocation id,
                                                   Function<Item.Properties, T> factory,
                                                   @Nullable ResourceKey<CreativeModeTab> group){

        Preconditions.checkArgument(id.getNamespace().equals(CTDMod.MODID), "Namespace mismatch!");

        var deferredItem = DR.registerItem(id.getPath(), factory);

        var definition = new ItemDefinition<>(name, deferredItem);

        if (Objects.equals(group, CTDCreativeTabIds.MAIN)) {
            MainCreativeTab.add(definition);
        } else if (group != null) {
            MainCreativeTab.add(definition);
            MainCreativeTab.addExternal(group, definition);
        }

        ITEMS.add(definition);
        return definition;
    }
}
