package data;
import cls.items.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FoodDataRegistry {
    private static final List<Food> ALL_FOOD_ITEMS = new ArrayList<>();

    static {
        ALL_FOOD_ITEMS.add(new Food("Fish n' Chips", 50, 150, 135));
        ALL_FOOD_ITEMS.add(new Food("Baguette", 25, 100, 80));
        ALL_FOOD_ITEMS.add(new Food("Sashimi", 70, 300, 275));
        ALL_FOOD_ITEMS.add(new Food("Fugu", 50, 0, 135));
        ALL_FOOD_ITEMS.add(new Food("Wine", 20, 100, 90));
        ALL_FOOD_ITEMS.add(new Food("Pumpkin Pie", 35, 120, 100));
        ALL_FOOD_ITEMS.add(new Food("Veggie Soup", 40, 140, 120));
        ALL_FOOD_ITEMS.add(new Food("Fish Stew", 70, 280, 260));
        ALL_FOOD_ITEMS.add(new Food("Spakbor Salad", 70, 0, 250));
        ALL_FOOD_ITEMS.add(new Food("Fish Sandwich", 50, 200, 180));
        ALL_FOOD_ITEMS.add(new Food("The Legends of Spakbor", 100, 0, 2000));
        ALL_FOOD_ITEMS.add(new Food("Cooked Pig's Head", 100, 1000, 0)); // Sell price is 0g
    }

    /**
     * Gets a list of all defined food items.
     * These are the "prototypes" of the food.
     * @return A new list containing all food definitions.
     */
    public static List<Food> getAllFoodItems() {
        return new ArrayList<>(ALL_FOOD_ITEMS);
    }

    /**
     * Gets a specific food item by its exact name (case-insensitive).
     * @param name The name of the food.
     * @return The Food object (prototype) if found, otherwise null.
     */
    public static Food getFoodByName(String name) {
        for (Food food : ALL_FOOD_ITEMS) {
            if (food.getName().equalsIgnoreCase(name)) {
                return food;
            }
        }
        System.err.println("Warning: Food with name '" + name + "' not found in FoodDataRegistry.");
        return null;
    }

    /**
     * Gets a list of food items that can be purchased (i.e., have a buy price > 0).
     * @return A list of purchasable food items.
     */
    public static List<Food> getPurchasableFood() {
        return ALL_FOOD_ITEMS.stream()
                .filter(food -> food.getBuyPrice() > 0)
                .collect(Collectors.toList());
    }
}
