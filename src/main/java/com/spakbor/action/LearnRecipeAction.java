package com.spakbor.action;
import com.spakbor.cls.core.*;
import com.spakbor.cls.items.*;

public class LearnRecipeAction implements Action {
    private static final long serialVersionUID = 1L;
    private RecipeItem recipeItemToLearn;

    public LearnRecipeAction(RecipeItem recipeItem) {
        this.recipeItemToLearn = recipeItem;
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (recipeItemToLearn == null) {
            System.out.println("Tidak ada item resep yang dipilih.");
            return false;
        }
        if (!player.getInventory().hasItem(recipeItemToLearn) || player.getInventory().getItemQuantity(recipeItemToLearn) < 1) {
            System.out.println("Tidak memiliki '" + recipeItemToLearn.getName() + "' di inventory.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        player.learnRecipe(recipeItemToLearn.getRecipeIdToUnlock());
        player.getInventory().useItem(recipeItemToLearn, 1);
    }
}