package com.spakbor.data;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.spakbor.cls.items.Misc;
import com.spakbor.cls.items.RecipeItem;

public class MiscDataRegistry {
    private static final List<Misc> ALL_MISC_ITEMS = new ArrayList<>();

    static {
        ALL_MISC_ITEMS.add(new Misc("Firewood", 20, 5));

        ALL_MISC_ITEMS.add(new Misc("Coal", 20, 15));

        ALL_MISC_ITEMS.add(new Misc("Egg", 30, 10));

        ALL_MISC_ITEMS.add(new Misc("Proposal Ring", 10000, 2000));

        ALL_MISC_ITEMS.add(new RecipeItem("Resep Fish n' Chips", 200, "recipe_1"));

        ALL_MISC_ITEMS.add(new RecipeItem("Resep Fish Sandwich", 150, "recipe_10"));
    }

    /**
     * Mendapatkan daftar semua item misc. yang terdefinisi.
     * @return Sebuah List baru yang berisi semua item misc.
     */
    public static List<Misc> getAllMiscItems() {
        return new ArrayList<>(ALL_MISC_ITEMS);
    }

    /**
     * Mendapatkan item misc. spesifik berdasarkan namanya (tidak case-sensitive).
     * @param name Nama item misc. yang dicari.
     * @return Objek Misc jika ditemukan, jika tidak null.
     */
    public static Misc getMiscItemByName(String name) {
        for (Misc item : ALL_MISC_ITEMS) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Mendapatkan daftar item misc. yang dapat dibeli (harga beli > 0).
     * @return Sebuah List item misc. yang dapat dibeli.
     */
    public static List<Misc> getPurchasableMiscItems() {
        return ALL_MISC_ITEMS.stream()
                .filter(item -> item.getBuyPrice() > 0)
                .collect(Collectors.toList());
    }
}
