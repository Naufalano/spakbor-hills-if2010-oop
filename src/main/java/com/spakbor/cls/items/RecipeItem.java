package com.spakbor.cls.items;

public class RecipeItem extends Misc {
    private static final long serialVersionUID = 1L;

    private String recipeIdToUnlock;

    /**
     * Konstruktor untuk item resep.
     * @param name Nama item resep (misalnya, "Resep Fish n' Chips").
     * @param buyPrice Harga beli resep di toko.
     * @param recipeIdToUnlock ID dari resep di RecipeDataRegistry yang akan terbuka.
     */
    public RecipeItem(String name, int buyPrice, String recipeIdToUnlock) {
        super(name, buyPrice, 0);
        this.recipeIdToUnlock = recipeIdToUnlock;
    }

    public String getRecipeIdToUnlock() {
        return recipeIdToUnlock;
    }

    @Override
    public void use() {
        System.out.println("Melihat resep untuk: " + getName().replace("Resep ", "") + ".");
        System.out.println("Gunakan perintah 'learnrecipe " + getName() + "' untuk mempelajarinya dari inventory.");
    }
}
