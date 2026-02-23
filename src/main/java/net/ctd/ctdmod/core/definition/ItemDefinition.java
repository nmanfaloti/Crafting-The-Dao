package net.ctd.ctdmod.core.definition;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Holds a registered item with a readable name and convenience methods for stacks and identity.
 *
 * @param <T> the item type
 */
public class ItemDefinition<T extends Item> implements ItemLike, Supplier<T> {
    private final String englishName;
    private final DeferredHolder<Item, T> item;

    /**
     * Creates an item definition.
     *
     * @param englishName The readable English name of the item.
     * @param item        The deferred holder for the item.
     */
    public ItemDefinition(String englishName, DeferredHolder<Item, T> item) {
        this.englishName = englishName;
        this.item = item;
    }

    /** Returns the readable English name of the item. */
    public String getEnglishName() {
        return englishName;
    }

    /** Returns the registry {@link ResourceLocation} of the item. */
    public ResourceLocation id() {
        return this.item.getId();
    }

    /** Returns a stack of one of this item. */
    public ItemStack stack() {
        return stack(1);
    }

    /** Returns a stack of the given size. */
    public ItemStack stack(int stackSize) {
        return new ItemStack(item.value(), stackSize);
    }

    /** Returns whether the given stack's item is this definition's item. */
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
