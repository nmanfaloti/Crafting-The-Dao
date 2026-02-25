package net.ctd.ctdmod.customrecipe;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.level.block.entity.BlockEntity;

public class CTDBaseRecipe {
    public final String name;
    public final List<CTDIngredient> ingredients;
    public final Supplier<?> resultFactory;
    public final int craftTime;

    public CTDBaseRecipe(String name, List<CTDIngredient> ingredients, Supplier<?> resultFactory, int craftTime) {
        this.name = name;
        this.ingredients = ingredients;
        this.resultFactory = resultFactory;
        this.craftTime = craftTime;
    }

    // This method can be overridden by specific recipes to perform actions when crafting ends
    public void CraftEnded(BlockEntity blockEntity, Object result) {
        // Exemple : jouer un effet à la position du block
        // BlockPos pos = blockEntity.getBlockPos();
        // Level level = blockEntity.getLevel();
    }
}