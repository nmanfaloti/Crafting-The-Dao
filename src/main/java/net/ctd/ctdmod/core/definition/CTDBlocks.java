package net.ctd.ctdmod.core.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import net.ctd.ctdmod.CTDMod;
import net.ctd.ctdmod.api.CTDCreativeTabIds;
import net.ctd.ctdmod.block.CTDBaseBlock;
import net.ctd.ctdmod.block.CTDBaseBlockItem;
import net.ctd.ctdmod.block.misc.SpiritStones;
import net.ctd.ctdmod.blockentity.entity.alchemy.AlchemyCauldron;
import net.ctd.ctdmod.blockentity.entity.alchemy.AlchemyCauldronEntity;
import net.ctd.ctdmod.core.MainCreativeTab;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Central registry for all custom blocks in the mod.
 * <p>
 * Blocks are registered via {@link #DR}; each block gets a corresponding {@link BlockItem} unless
 * a custom item factory is provided. Use the private {@code block(...)} methods to register new blocks.
 */
public class CTDBlocks {
    public static final DeferredRegister.Blocks DR = DeferredRegister.createBlocks(CTDMod.MODID);

    public static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    // -------------------------------------------------------------------------
    // BLOCKS
    // -------------------------------------------------------------------------

    public static final BlockDefinition<AlchemyCauldron> ALCHEMY_CAULDRON = block(
            "alchemy_cauldron",
            ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "alchemy_cauldron"),
            props -> new AlchemyCauldron(props.noOcclusion().mapColor(MapColor.STONE)),
            CTDCreativeTabIds.ALCHEMY
    );

    public static final BlockDefinition<SpiritStones> SPIRIT_STONES = block(
            "spirit_stones",
            ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "spirit_stones"),
            props -> new SpiritStones(props.noOcclusion().mapColor(MapColor.STONE)),
            CTDCreativeTabIds.ALCHEMY
    );

    // -------------------------------------------------------------------------
    // BLOCKS ENTITY TEST
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Registration API
    // -------------------------------------------------------------------------

    /**
     * Returns an unmodifiable list of all registered block definitions.
     *
     * @return the list of {@link BlockDefinition}s
     */
    public static List<BlockDefinition<?>> getBlocks(){
        return Collections.unmodifiableList(BLOCKS);
    }

    /**
     * Registers a block and its item with default item factory, no creative tab.
     *
     * @param englishName   The readable English name of the block.
     * @param id            The {@link ResourceLocation} for the block (this mod's namespace).
     * @param blockSupplier Factory for the block from {@link Properties}.
     * @return A {@link BlockDefinition} for the block and its item.
     */
    private static <T extends Block> BlockDefinition<T> block(final String englishName, final ResourceLocation id,  final Function<Properties, T> blockSupplier){
        return block(englishName, id, blockSupplier, null, null);
    }

    /**
     * Registers a block and its item with default item factory and optional creative tab.
     *
     * @param englishName   The readable English name of the block.
     * @param id            The {@link ResourceLocation} for the block (this mod's namespace).
     * @param blockSupplier Factory for the block from {@link Properties}.
     * @param group         The creative tab for the block's item, or {@code null}.
     * @return A {@link BlockDefinition} for the block and its item.
     */
    private static <T extends Block> BlockDefinition<T> block(final String englishName, final ResourceLocation id,  final Function<Properties, T> blockSupplier, final ResourceKey<CreativeModeTab> group){
        return block(englishName, id, blockSupplier, null, group);
    }

    /**
    * Registers a new block and its corresponding item.
    *
    * <p>This method creates and registers a block through {@link net.neoforged.neoforge.registries.DeferredRegister},
    * and automatically creates a matching {@link BlockItem}, unless a custom item factory is provided.</p>
    *
    * @param englishName   The readable English name of the block (for internal and localization use).
    * @param id            The {@link ResourceLocation} identifying the block (must belong to this mod's namespace).
    * @param blockSupplier A factory function that takes {@link Properties} and returns the block instance.
    * @param itemFactory   (Optional) A factory to create a custom {@link BlockItem}. If {@code null}, a default item is used.
    * @param group         (Optional) The creative mode tab where the block’s item will appear. If {@code null}, it is added to the main CTD tab.
    * 
    * @return A {@link BlockDefinition} object containing both the block and its registered item.
    * 
    * @throws IllegalArgumentException If the given {@code id} does not belong to this mod’s namespace.
    * @throws IllegalStateException    If a non-null {@code itemFactory} returns a null item.
    */
    private static <T extends Block> BlockDefinition<T> block(
            final String englishName,
            final ResourceLocation id,
            final Function<Properties, T> blockSupplier,
            @Nullable final BiFunction<Block, Item.Properties, BlockItem> itemFactory,
            @Nullable final ResourceKey<CreativeModeTab> group) {
        Preconditions.checkArgument(id.getNamespace().equals(CTDMod.MODID), "Can only register for CTDMod");

        // Enregistrement du bloc via NeoForge DeferredRegister
        CTDMod.LOGGER.info("Registering block " + id + "path : " + id.getPath());
        final var deferredBlock = DR.registerBlock(id.getPath(), blockSupplier);
        final var deferredItem = CTDItems.DR.registerItem(id.getPath(), (properties) -> {
            final var block = deferredBlock.get();
            if (itemFactory != null) {
                final var item = itemFactory.apply(block, properties);
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

        final var itemDef = new ItemDefinition<>(englishName, deferredItem);

        if (group != null) {
            MainCreativeTab.addExternal(group, itemDef);
        } else {
            MainCreativeTab.add(itemDef);
        }
        final BlockDefinition<T> definition = new BlockDefinition<>(englishName, deferredBlock, itemDef);

        BLOCKS.add(definition);

        return definition;
    }

}
