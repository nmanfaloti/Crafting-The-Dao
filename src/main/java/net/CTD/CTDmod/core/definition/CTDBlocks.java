package net.CTD.CTDmod.core.definition;

import com.google.common.base.Preconditions;
import net.CTD.CTDmod.CTDMod;
import net.CTD.CTDmod.api.CTDCreativeTabIds;
import net.CTD.CTDmod.block.CTDBaseBlock;
import net.CTD.CTDmod.block.CTDBaseBlockItem;
import net.CTD.CTDmod.block.misc.AlchemyCauldron;
import net.CTD.CTDmod.core.MainCreativeTab;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class CTDBlocks {
    public static final DeferredRegister<Block> DR = DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK, CTDMod.MODID);

    public static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    // PROPERTIES


    //
    // BLOCKS
    //

    public static final BlockDefinition<AlchemyCauldron> ALCHEMY_CAULDRON = block(
            "alchemy_cauldron",
            ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "alchemy_cauldron"),
            () -> new AlchemyCauldron(CTDBaseBlock.stoneProps().noOcclusion()),
            CTDCreativeTabIds.ALCHEMY
    );

    //
    //
    //

    public static List<BlockDefinition<?>> getBlocks(){
        return Collections.unmodifiableList(BLOCKS);
    }


    private static <T extends Block> BlockDefinition<T> block(String englishName, ResourceLocation id, Supplier<T> blockSupplier){
        return block(englishName,id,blockSupplier, null, null);
    }

    private static <T extends Block> BlockDefinition<T> block(String englishName, ResourceLocation id, Supplier<T> blockSupplier, ResourceKey<CreativeModeTab> group){
        return block(englishName,id,blockSupplier, null, group);
    }

    private static <T extends Block> BlockDefinition<T> block(
            String englishName,
            ResourceLocation id,
            Supplier<T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory,
            @Nullable ResourceKey<CreativeModeTab> group) {
        Preconditions.checkArgument(id.getNamespace().equals(CTDMod.MODID), "Can only register for TutoMod");

        // Enregistrement du bloc via NeoForge DeferredRegister
        var deferredBlock = DR.register(id.getPath(), blockSupplier);
        var deferredItem = CTDItems.DR.register(id.getPath(), () -> {
            var block = deferredBlock.get();
            var itemProperties = new Item.Properties();
            if (itemFactory != null) {
                var item = itemFactory.apply(block, itemProperties);
                if (item == null) {
                    throw new IllegalStateException("The item factory returned null for block " + id);
                }
                return item;
            } else if (block instanceof CTDBaseBlock) {
                return new CTDBaseBlockItem(block, itemProperties);
            } else {
                return new BlockItem(block, itemProperties);
            }
        });

        var itemDef = new ItemDefinition<>(englishName, deferredItem);

        if (group != null) {
            MainCreativeTab.addExternal(group, itemDef);
        } else {
            MainCreativeTab.add(itemDef);
        }
        BlockDefinition<T> definition = new BlockDefinition<>(englishName, deferredBlock, itemDef);

        BLOCKS.add(definition);

        return definition;
    }

}
