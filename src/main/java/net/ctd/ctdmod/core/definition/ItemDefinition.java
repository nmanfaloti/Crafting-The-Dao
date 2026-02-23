package net.ctd.ctdmod.core.definition;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class ItemDefinition<T extends Item> implements ItemLike, Supplier<T> {
    private final String englishName;
    private final DeferredHolder<Item, T> item;

    public ItemDefinition(String englishName, DeferredHolder<Item, T> item) {
        this.englishName = englishName;
        this.item = item;
    }

    public String getEnglishName() {
        return englishName;
    }

    public ResourceLocation id() {
        return this.item.getId();
    }

    public ItemStack stack() {
        return stack(1);
    }

    public ItemStack stack(int stackSize) {
        return new ItemStack(item.value(), stackSize);
    }

    public boolean is(ItemStack comparableStack){
        return item.value().equals(comparableStack.getItem());
    }

    @Override
    public T get() {
        return item.value();
    }

    @Override
    public @NotNull T asItem() {
        return item.value();
    }

    @Override
    public String toString() {
        return "ItemDefinition{" +
                "englishName='" + englishName + '\'' +
                ", id=" + item.getId() +
                '}';
    }
}
