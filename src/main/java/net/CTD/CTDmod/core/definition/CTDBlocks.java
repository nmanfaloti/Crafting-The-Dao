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
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class CTDBlocks {
    public static final DeferredRegister.Blocks DR = DeferredRegister.createBlocks(CTDMod.MODID);

    public static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    // PROPERTIES


    //
    // BLOCKS
    //

    public static final BlockDefinition<AlchemyCauldron> ALCHEMY_CAULDRON = block(
            "alchemy_cauldron",
            ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "alchemy_cauldron"),
            props -> new AlchemyCauldron(props.noOcclusion().mapColor(MapColor.STONE)),
            CTDCreativeTabIds.ALCHEMY
    );

    //
    //
    //

    public static List<BlockDefinition<?>> getBlocks(){
        return Collections.unmodifiableList(BLOCKS);
    }


    private static <T extends Block> BlockDefinition<T> block(String englishName, ResourceLocation id,  Function<Properties, T> blockSupplier){
        return block(englishName,id,blockSupplier, null, null);
    }

    private static <T extends Block> BlockDefinition<T> block(String englishName, ResourceLocation id,  Function<Properties, T> blockSupplier, ResourceKey<CreativeModeTab> group){
        return block(englishName,id,blockSupplier, null, group);
    }

    private static <T extends Block> BlockDefinition<T> block(
            String englishName,
            ResourceLocation id,
            Function<Properties, T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory,
            @Nullable ResourceKey<CreativeModeTab> group) {
        Preconditions.checkArgument(id.getNamespace().equals(CTDMod.MODID), "Can only register for TutoMod");

        // Enregistrement du bloc via NeoForge DeferredRegister
        var deferredBlock = DR.registerBlock(id.getPath(), blockSupplier);
        var deferredItem = CTDItems.DR.registerItem(id.getPath(), (properties) -> {
            var block = deferredBlock.get();
            if (itemFactory != null) {
                var item = itemFactory.apply(block, properties);
                if (item == null) {
                    throw new IllegalStateException("The item factory returned null for block " + id);
                }
                return item;
            } else if (block instanceof CTDBaseBlock) {
                return new CTDBaseBlockItem(block, properties);
            } else {
                return new BlockItem(block, properties);
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
