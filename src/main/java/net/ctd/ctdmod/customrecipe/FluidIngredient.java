package net.ctd.ctdmod.customrecipe;

import net.minecraft.world.level.material.Fluid;
public class FluidIngredient implements CTDIngredient {
    public final Fluid fluid;
    public final int amount;
    public FluidIngredient(Fluid fluid, int amount) {
        this.fluid = fluid;
        this.amount = amount;
    }

    public int getCount() {
        return amount;
    }
    public int getType() {
        return 1;
    }
    public Object getIngredient() {
        return fluid;
    }
}