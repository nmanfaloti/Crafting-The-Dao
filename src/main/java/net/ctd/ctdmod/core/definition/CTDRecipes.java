package net.ctd.ctdmod.core.definition;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

import net.ctd.ctdmod.customrecipe.AlchemyRecipe;
import net.ctd.ctdmod.customrecipe.CTDBaseRecipe;
import net.ctd.ctdmod.customrecipe.ItemIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


public class CTDRecipes {
    public static final Map<String, List<CTDBaseRecipe>> RECIPES = new HashMap<>();


    static {
        // Example recipe registration
        createRecipe(new AlchemyRecipe(
            "alchemy_diamond",
            List.of(
                new ItemIngredient(new ItemStack(Items.IRON_INGOT, 2)),
                new ItemIngredient(new ItemStack(Items.GOLD_INGOT, 1))
            ),
            () -> new ItemStack(Items.DIAMOND, 3),
            20
        ));
    }   

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    public static <T extends CTDBaseRecipe> List<T> getRecipes(Class<T> recipeType) {
        String className = recipeType.getSimpleName();
        return (List<T>) RECIPES.getOrDefault(className, new ArrayList<>());
    }

    public static <T extends CTDBaseRecipe> void createRecipe(T recipe) {
        Preconditions.checkNotNull(recipe, "Recipe cannot be null");
        String className = recipe.getClass().getSimpleName();
        RECIPES.computeIfAbsent(className, k -> new ArrayList<>()).add(recipe);
    }
}
