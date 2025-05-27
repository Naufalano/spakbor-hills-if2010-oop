package cls.core;
import cls.items.*;
import java.util.Map;

public class Recipe {
    private String recipeId; 
    private String cookedItemName; 
    private Map<String, Integer> ingredients; 
    private Food resultItem;
    private String unlockConditionDescription;

    public static final String ANY_FISH_INGREDIENT = "Any Fish";
    // public static final String ANY_CROP_INGREDIENT = "Any Crop"; 

    public static final int COOKING_DURATION_MINUTES = 60;
    public static final int COOKING_INITIATION_ENERGY = 10;


    public Recipe(String recipeId, String cookedItemName, Map<String, Integer> ingredients,
                  Food resultItem, String unlockConditionDescription) {
        this.recipeId = recipeId;
        this.cookedItemName = cookedItemName;
        this.ingredients = ingredients;
        this.resultItem = resultItem;
        this.unlockConditionDescription = unlockConditionDescription;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public String getCookedItemName() {
        return cookedItemName;
    }

    public Map<String, Integer> getIngredients() {
        return ingredients;
    }

    public Food getResultItem() {
        return resultItem;
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
               ", resultItem=" + resultItem.getName() + 
               ", unlock='" + unlockConditionDescription + '\'' +
               '}';
    }
}
