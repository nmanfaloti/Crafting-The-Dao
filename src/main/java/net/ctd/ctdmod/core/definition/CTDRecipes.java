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


    public static void init() {
        // Example recipe registration
        createRecipe(new AlchemyRecipe(
            "alchemy_diamond",
            List.of(
                new ItemIngredient(new ItemStack(Items.IRON_INGOT, 2)),
                new ItemIngredient(new ItemStack(Items.GOLD_INGOT, 1))
            ),
            () -> new ItemStack(Items.DIAMOND, 1),
            20
        ));
    }   


    /*
     * Get a recipe by its name. Returns null if no recipe with the given name exists.
     * @param recipeClass The class of the recipe to retrieve
     * @param name The name of the recipe to retrieve
     * @return The recipe with the given name, or null if no such recipe exists
     */
    public static <T extends CTDBaseRecipe> T getRecipeByName(Class<T> recipeClass, String name) {
        return getRecipes(recipeClass).stream()
                .filter(recipe -> recipe.name.equals(name))
                .findFirst()
                .orElse(null);
    }

    /*
     * Get all recipes of a specific type.
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
