package net.CTD.CTDmod.core.definition;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;

public class BlockDefinition<T extends Block> implements ItemLike {
    private final String englishName;
    private final ItemDefinition<BlockItem> item;
    private final DeferredHolder<Block, T> block;

    public BlockDefinition(String englishName, DeferredHolder<Block, T> block, ItemDefinition<BlockItem> item) {
        this.englishName = englishName;
        this.item = Objects.requireNonNull(item, "item");
        this.block = Objects.requireNonNull(block, "block");
    }

    public String getEnglishName(){
        return englishName;
    }

    public ResourceLocation id(){
        return block.getId();
    }

    public final T block(){
        return this.block.value();
    }

    public ItemStack stack(){
        return item.stack();
    }

    public ItemStack stack(int stackSize) {
        return item.stack(stackSize);
    }

    public boolean is(ItemStack comparableStack){
        return item.is(comparableStack);
    }

    public ItemDefinition<BlockItem> item(){
        return item;
    }

    @Override
    public Item asItem(){
        return item.asItem();
    }
}
