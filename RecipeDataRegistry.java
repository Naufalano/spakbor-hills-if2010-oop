import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Assuming Recipe.java, Food.java, Item.java, CropDataRegistry.java (for ingredient names),
// FishDataRegistry.java (for ingredient names like "Salmon", "Pufferfish", "Legend") are accessible.
// Also assumes Misc.java exists for items like "Egg".

public class RecipeDataRegistry {
    private static final List<Recipe> ALL_RECIPES = new ArrayList<>();

    static {
        // Helper to create ingredient maps
        Map<String, Integer> ingredients;

        // --- Define Resulting Food Items (with placeholder energy/prices) ---
        // You'll need to adjust these Food item properties for game balance.
        // Food(name, energyRestoration, buyPrice (usually 0 for crafted), sellPrice)

        Food fishNChipsFood = new Food("Fish n' Chips", 75, 0, 150);
        Food baguetteFood = new Food("Baguette", 40, 0, 60);
        Food sashimiFood = new Food("Sashimi", 60, 0, 200);
        Food fuguFood = new Food("Fugu", 100, 0, 500); // Fugu is special, maybe high energy or unique effect
        Food wineFood = new Food("Wine", 30, 0, 120); // Wine might have other effects too
        Food pumpkinPieFood = new Food("Pumpkin Pie", 80, 0, 280);
        Food veggieSoupFood = new Food("Veggie Soup", 65, 0, 180);
        Food fishStewFood = new Food("Fish Stew", 90, 0, 220);
        Food spakborSaladFood = new Food("Spakbor Salad", 120, 0, 350);
        Food fishSandwichFood = new Food("Fish Sandwich", 85, 0, 190);
        Food legendsOfSpakborFood = new Food("The Legends of Spakbor", 200, 0, 1000); // Legendary dish

        // --- Initialize Recipes ---

        // recipe_1: Fish n' Chips
        ingredients = new HashMap<>();
        ingredients.put(Recipe.ANY_FISH_INGREDIENT, 2); // Special key for "Any Fish"
        ingredients.put("Wheat", 1);   // Assumes "Wheat" is a Crop item name
        ingredients.put("Potato", 1);  // Assumes "Potato" is a Crop item name
        ALL_RECIPES.add(new Recipe("recipe_1", "Fish n' Chips", ingredients, fishNChipsFood, "Beli di store"));

        // recipe_2: Baguette
        ingredients = new HashMap<>();
        ingredients.put("Wheat", 3);
        ALL_RECIPES.add(new Recipe("recipe_2", "Baguette", ingredients, baguetteFood, "Default/Bawaan"));

        // recipe_3: Sashimi
        ingredients = new HashMap<>();
        ingredients.put("Salmon", 3); // Assumes "Salmon" is a Fish item name
        ALL_RECIPES.add(new Recipe("recipe_3", "Sashimi", ingredients, sashimiFood, "Setelah memancing 10 ikan"));

        // recipe_4: Fugu
        ingredients = new HashMap<>();
        ingredients.put("Pufferfish", 1); // Assumes "Pufferfish" is a Fish item name
        ALL_RECIPES.add(new Recipe("recipe_4", "Fugu", ingredients, fuguFood, "Memancing pufferfish"));

        // recipe_5: Wine
        ingredients = new HashMap<>();
        ingredients.put("Grape", 2); // Assumes "Grape" is a Crop item name
        ALL_RECIPES.add(new Recipe("recipe_5", "Wine", ingredients, wineFood, "Default/Bawaan"));

        // recipe_6: Pumpkin Pie
        ingredients = new HashMap<>();
        ingredients.put("Egg", 1);       // Assuming "Egg" is an Item (e.g., Misc)
        ingredients.put("Wheat", 1);
        ingredients.put("Pumpkin", 1);   // Assumes "Pumpkin" is a Crop item name
        ALL_RECIPES.add(new Recipe("recipe_6", "Pumpkin Pie", ingredients, pumpkinPieFood, "Default/Bawaan"));

        // recipe_7: Veggie Soup
        ingredients = new HashMap<>();
        ingredients.put("Cauliflower", 1);
        ingredients.put("Parsnip", 1);
        ingredients.put("Potato", 1);
        ingredients.put("Tomato", 1);
        ALL_RECIPES.add(new Recipe("recipe_7", "Veggie Soup", ingredients, veggieSoupFood, "Memanen untuk pertama kalinya"));

        // recipe_8: Fish Stew
        ingredients = new HashMap<>();
        ingredients.put(Recipe.ANY_FISH_INGREDIENT, 2);
        ingredients.put("Hot Pepper", 1);
        ingredients.put("Cauliflower", 2);
        ALL_RECIPES.add(new Recipe("recipe_8", "Fish Stew", ingredients, fishStewFood, "Dapatkan \"Hot Pepper\" terlebih dahulu"));

        // recipe_9: Spakbor Salad
        ingredients = new HashMap<>();
        ingredients.put("Melon", 1);
        ingredients.put("Cranberry", 1);
        ingredients.put("Blueberry", 1);
        ingredients.put("Tomato", 1);
        ALL_RECIPES.add(new Recipe("recipe_9", "Spakbor Salad", ingredients, spakborSaladFood, "Default/Bawaan"));

        // recipe_10: Fish Sandwich
        ingredients = new HashMap<>();
        ingredients.put(Recipe.ANY_FISH_INGREDIENT, 1);
        ingredients.put("Wheat", 2);
        ingredients.put("Tomato", 1);
        ingredients.put("Hot Pepper", 1);
        ALL_RECIPES.add(new Recipe("recipe_10", "Fish Sandwich", ingredients, fishSandwichFood, "Beli di store"));

        // recipe_11: The Legends of Spakbor
        ingredients = new HashMap<>();
        ingredients.put("Legend", 1);    // Assumes "Legend" is a specific Fish item name
        ingredients.put("Potato", 2);
        ingredients.put("Parsnip", 1);
        ingredients.put("Tomato", 1);
        ingredients.put("Eggplant", 1);  // Assuming "Eggplant" is an Item (e.g., Misc or new Crop)
        ALL_RECIPES.add(new Recipe("recipe_11", "The Legends of Spakbor", ingredients, legendsOfSpakborFood, "Memancing \"Legend\""));
    }

    /**
     * Gets a list of all defined recipes.
     * @return A new list containing all recipes.
     */
    public static List<Recipe> getAllRecipes() {
        return new ArrayList<>(ALL_RECIPES); // Return a copy
    }

    /**
     * Gets a specific recipe by its exact item ID (e.g., "recipe_1").
     * @param recipeId The ID of the recipe.
     * @return The Recipe object if found, otherwise null.
     */
    public static Recipe getRecipeById(String recipeId) {
        for (Recipe recipe : ALL_RECIPES) {
            if (recipe.getRecipeId().equalsIgnoreCase(recipeId)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Gets a specific recipe by the name of the item it produces.
     * @param cookedItemName The name of the cooked food item.
     * @return The Recipe object if found, otherwise null.
     */
    public static Recipe getRecipeByCookedItemName(String cookedItemName) {
        for (Recipe recipe : ALL_RECIPES) {
            if (recipe.getCookedItemName().equalsIgnoreCase(cookedItemName)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Gets a list of recipes that the player might be able to make based on currently available (unlocked) recipes.
     * This method doesn't check for ingredients, only for unlock status.
     * The actual unlocking logic would be managed elsewhere (e.g., Player class or a quest system).
     * For now, this might return all default recipes or recipes based on simple player stats.
     * @param player The player (to check their unlocked recipes or game progression).
     * @return A list of currently available recipes to the player.
     */
    public static List<Recipe> getAvailableRecipesForPlayer(Player player) {
        // This is a placeholder for actual unlock logic.
        // For now, let's return recipes marked as "Default/Bawaan" or that don't require store purchase.
        return ALL_RECIPES.stream()
                .filter(recipe -> recipe.getUnlockConditionDescription().equalsIgnoreCase("Default/Bawaan") ||
                                 !recipe.getUnlockConditionDescription().equalsIgnoreCase("Beli di store")
                                 // Add more complex unlock checks here based on player stats/achievements
                                 // e.g., player.hasUnlockedRecipe(recipe.getRecipeId())
                )
                .collect(Collectors.toList());
    }
}
