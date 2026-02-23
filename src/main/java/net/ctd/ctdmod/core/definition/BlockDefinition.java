package net.ctd.ctdmod.core.definition;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;

/**
 * Holds a registered block and its corresponding {@link BlockItem}, with a readable name and
 * convenience methods for stacks and identity checks.
 *
 * @param <T> the block type
 */
public class BlockDefinition<T extends Block> implements ItemLike {
    private final String englishName;
    private final ItemDefinition<BlockItem> item;
    private final DeferredHolder<Block, T> block;

    /**
     * Creates a block definition.
     *
     * @param englishName The readable English name of the block.
     * @param block       The deferred holder for the block.
     * @param item        The definition of the block's item; must not be {@code null}.
     * @throws NullPointerException if {@code item} or {@code block} is null.
     */
    public BlockDefinition(String englishName, DeferredHolder<Block, T> block, ItemDefinition<BlockItem> item) {
        this.englishName = englishName;
        this.item = Objects.requireNonNull(item, "item");
        this.block = Objects.requireNonNull(block, "block");
    }

    /** Returns the readable English name of the block. */
    public String getEnglishName(){
        return englishName;
    }

    /** Returns the registry {@link ResourceLocation} of the block. */
    public ResourceLocation id(){
        return block.getId();
    }

    /** Returns the block instance. */
    public final T block(){
        return this.block.value();
    }

    /** Returns a stack of one of this block's item. */
    public ItemStack stack(){
        return item.stack();
    }

    /** Returns a stack of the given size of this block's item. */
    public ItemStack stack(int stackSize) {
        return item.stack(stackSize);
    }

    /** Returns whether the given stack is this block's item. */
    public boolean is(ItemStack comparableStack){
        return item.is(comparableStack);
    }

    /** Returns the item definition for the block's item. */
    public ItemDefinition<BlockItem> item(){
        return item;
    }

    @Override
    public Item asItem(){
        return item.asItem();
    }
}
