package net.ctd.ctdmod.items;


import net.ctd.ctdmod.core.definition.CTDDataComponents;
import net.minecraft.world.item.ItemStack;

public class GradedItemStack {
    private final ItemStack stack;
    private int grade;

    public GradedItemStack(ItemStack stack, int grade) {
        this.stack = stack;
        this.grade = grade;
        updateComponent();
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
        updateComponent();
    }

    public void updateComponent() {
        stack.set(CTDDataComponents.GRADE.get(), grade);
    }

    public static int getGradeFromStack(ItemStack stack) {
        return stack.getOrDefault(CTDDataComponents.GRADE.get(), 0);
    }

    public static GradedItemStack createFromStack(ItemStack stack, int grade) {
        GradedItemStack gradedStack = new GradedItemStack(stack, grade);
        return gradedStack;
    }
}