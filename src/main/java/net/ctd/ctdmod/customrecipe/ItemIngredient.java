package net.ctd.ctdmod.customrecipe;

import net.minecraft.world.item.ItemStack;
public class ItemIngredient implements CTDIngredient {
    private final ItemStack stack;
    public ItemIngredient(ItemStack stack) { 
        this.stack = stack;
    }
    public int getCount() {
        return stack.getCount();
    }
    public ItemStack getIngredient() {
        return stack;
    }
    public int getType() {
        return 0;
    }
}

