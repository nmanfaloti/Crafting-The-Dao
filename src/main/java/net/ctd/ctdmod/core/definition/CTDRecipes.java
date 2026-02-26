package net.ctd.ctdmod.core.definition;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

import net.ctd.ctdmod.customrecipe.AlchemyRecipe;
import net.ctd.ctdmod.customrecipe.CTDBaseRecipe;
import net.ctd.ctdmod.customrecipe.ItemIngredient;
import net.ctd.ctdmod.items.GradedItemStack;
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


    // Get the recipe by its name 
    public static AlchemyRecipe getAlchemyRecipeByName(String name) {
        return getRecipes(AlchemyRecipe.class).stream()
                .filter(r -> r.name.equals(name))
                .findFirst()
                .orElse(null);
    }

     /*
     * Generic method to get recipes by their class type, this allows us to easily retrieve all recipes of a certain type (e.g. all AlchemyRecipes)
      * We use the class name as the key in the RECIPES map, and we cast the result to the correct type
      * The unchecked warning is suppressed because we ensure type safety through the way we register recipes
      * If you try to get a recipe type that hasn't been registered, it will return an empty list instead of throwing an error
       * This makes it safe to call without having to check if the type exists first
      * Example usage: List<AlchemyRecipe> alchemyRecipes = CTDRecipes.getRecipes(AlchemyRecipe.class);
       * This will return a list of all registered AlchemyRecipe instances
      * You can also create new recipes using the createRecipe method, which will automatically add them to the correct list based on their class type

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
