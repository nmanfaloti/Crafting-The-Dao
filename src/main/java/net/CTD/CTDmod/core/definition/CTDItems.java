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
    public static final DeferredRegister<Item> DR = DeferredRegister.create(Registries.ITEM, CTDMod.MODID);

    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    //
    // DEV ITEMS
    //

    public static final ItemDefinition<Item> ELBABOSS = item(
            "elbaboss",
            ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "elbaboss"),
            Elbaboss::new
    );

    public static final ItemDefinition<Item> KATANA = item(
            "katana",
            ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "katana"),
            properties -> new Katana(),
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

        Preconditions.checkArgument(id.getNamespace().equals(CTDMod.MODID), "Can only register for TutoMod");
        var definition = new ItemDefinition<>(name, DR.register(id.getPath(), () -> factory.apply(new Item.Properties())));
        if (Objects.equals(group, CTDCreativeTabIds.MAIN)) {
            LOGGER.info("Registering item {} in the main creative tab", name);
            MainCreativeTab.add(definition);
        } else if (group != null) {
            LOGGER.info("Registering item {} in the creative tab {}", name, group.location());
            MainCreativeTab.addExternal(group, definition);
        }

        ITEMS.add(definition);

        ITEMS.forEach(item -> LOGGER.info("Key: {}, Value: {}", item.toString(), item));

        return definition;
    }
}

