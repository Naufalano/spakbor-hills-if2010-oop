import java.util.Map;

public class Recipe {
    private String recipeId; // e.g., "recipe_1"
    private String cookedItemName; // Name of the food produced, e.g., "Fish n' Chips"
    private Map<String, Integer> ingredients; // Key: Item Name (String), Value: Quantity (Integer)
    private Food resultItem; // The Food item that is the result of cooking
    private String unlockConditionDescription; // Textual description of how to unlock

    // Constants for special ingredient types
    public static final String ANY_FISH_INGREDIENT = "Any Fish";
    // public static final String ANY_CROP_INGREDIENT = "Any Crop"; // If needed in future

    // Duration of cooking is 1 hour (60 minutes) for all recipes as per PDF
    public static final int COOKING_DURATION_MINUTES = 60;
    // Energy cost for initiating cooking is 10
    public static final int COOKING_INITIATION_ENERGY = 10;


    public Recipe(String recipeId, String cookedItemName, Map<String, Integer> ingredients,
                  Food resultItem, String unlockConditionDescription) {
        this.recipeId = recipeId;
        this.cookedItemName = cookedItemName;
        this.ingredients = ingredients;
        this.resultItem = resultItem; // This Food object contains its own energy, sell price etc.
        this.unlockConditionDescription = unlockConditionDescription;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public String getCookedItemName() {
        return cookedItemName;
    }

    public Map<String, Integer> getIngredients() {
        return ingredients; // Returns the map of required ingredient names and quantities
    }

    public Food getResultItem() {
        return resultItem; // Returns the Food object that is created
    }

    public String getUnlockConditionDescription() {
        return unlockConditionDescription;
    }

    public int getTimeToCookMinutes() {
        return COOKING_DURATION_MINUTES;
    }

    @Override
    public String toString() {
        return "Recipe{" +
               "recipeId='" + recipeId + '\'' +
               ", cookedItemName='" + cookedItemName + '\'' +
               ", ingredients=" + ingredients +
               ", resultItem=" + resultItem.getName() + // Just show name for brevity
               ", unlock='" + unlockConditionDescription + '\'' +
               '}';
    }
}
