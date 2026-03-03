package net.ctd.ctdmod.customrecipe;

public interface CTDIngredient {
    public int getCount();
    public int getType(); // 0 for item, 1 for fluid, etc.
    public Object getIngredient(); // Return the actual ingredient (e.g., ItemStack for items, FluidStack for fluids)
}
