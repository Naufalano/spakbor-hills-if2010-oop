package com.spakbor.action;
import java.util.Map;

import com.spakbor.cls.core.Farm;
import com.spakbor.cls.core.OngoingCooking;
import com.spakbor.cls.core.Player;
import com.spakbor.cls.core.Recipe;
import com.spakbor.cls.items.Food;
import com.spakbor.cls.items.Item;
import com.spakbor.cls.world.FarmMap;
import com.spakbor.cls.world.GameMap;
import com.spakbor.cls.world.PlayerHouseMap;
import com.spakbor.utils.InteractionHelper;

public class CookingAction implements  Action {
    private static final long serialVersionUID = 1L;
    private Recipe recipeToCook;
    private static final String FIREWOOD_NAME = "Firewood";
    private static final String COAL_NAME = "Coal";

    public CookingAction(Recipe recipe) {
        this.recipeToCook = recipe;
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (recipeToCook == null) {
            System.out.println("Pilih resep.");
            return false;
        }

        GameMap map = farm.getCurrentMap();
        String adjacentObjectId = InteractionHelper.getAdjacentInteractableObject(player, map);
        if (!player.getCurrentLocationName().equals("Player's House") && !PlayerHouseMap.STOVE_ID.equals(adjacentObjectId)) {
            System.out.println("Cooking hanya valid di kompor rumah.");
            return false;
        }

        FarmMap farmMap = farm.getFarmMap();
        if (farmMap == null) {
            System.out.println("Farm map data tidak ada.");
            return false;
        }
        FarmMap.PlacedObject houseStructure = farmMap.getHouseStructureLocation();
        if (houseStructure == null) {
            System.out.println("House tidak ditemukan di map.");
            return false;
        }

        if (farm.getHouse() == null) {
            System.out.println("Ga bisa masak T_T");
            return false;
        }

        if (farm.isStoveBusy()) {
            OngoingCooking currentTask = farm.getCurrentCookingTaskInfo();
            if (currentTask != null && !currentTask.isReadyToClaim()) {
                 System.out.println("Kompor sedang digunakan untuk memasak " + currentTask.getCookedItemName() + ". Tunggu hingga matang.");
            } else if (currentTask != null && currentTask.isReadyToClaim()) {
                System.out.println(currentTask.getCookedItemName() + " sudah matang dan siap diambil. Kamu tidak bisa memasak lagi sebelum mengambilnya.");
            } else {
                 System.out.println("Kompor sedang digunakan."); // Fallback
            }
            return false;
        }

        // Check for ingredients
        for (Map.Entry<String, Integer> entry : recipeToCook.getIngredients().entrySet()) {
            boolean foundSufficient = false;
            for(Item itemInInv : player.getInventory().getInventoryMap().keySet()){
                if(itemInInv.getName().equalsIgnoreCase(entry.getKey()) && 
                   player.getInventory().getItemQuantity(itemInInv) >= entry.getValue()){
                    foundSufficient = true;
                    break;
                }
            }
            if(!foundSufficient){
                 System.out.println("Kurang bahan: " + entry.getValue() + "x " + entry.getKey());
                 return false;
            }
        }

        boolean hasFirewood = false;
        boolean hasCoal = false;
        for(Item itemInInv : player.getInventory().getInventoryMap().keySet()){
            if(FIREWOOD_NAME.equalsIgnoreCase(itemInInv.getName()) && player.getInventory().getItemQuantity(itemInInv) > 0) hasFirewood = true;
            if(COAL_NAME.equalsIgnoreCase(itemInInv.getName()) && player.getInventory().getItemQuantity(itemInInv) > 0) hasCoal = true;
        }
        if (!hasFirewood && !hasCoal) {
            System.out.println("Butuh bahan bakar buat masak (Coal atau Firewood).");
            return false;
        }

        if (player.getEnergy() + 20 < 10) {
            System.out.println("Masak butuh tenaga coy (-10 energy).");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        player.setEnergy(player.getEnergy() - 10);

        for (Map.Entry<String, Integer> entry : recipeToCook.getIngredients().entrySet()) {
            Item ingredientItem = null;
            for(Item item : player.getInventory().getInventoryMap().keySet()){
                 if(item.getName().equalsIgnoreCase(entry.getKey())){
                    ingredientItem = item;
                    break;
                }
            }
            if (ingredientItem != null) {
                player.getInventory().useItem(ingredientItem, entry.getValue());
            }
        }

        boolean fuelConsumed = false;
        Item coal = null;
        Item firewood = null;

        for(Item item : player.getInventory().getInventoryMap().keySet()){
            if(COAL_NAME.equalsIgnoreCase(item.getName())) coal = item;
            if(FIREWOOD_NAME.equalsIgnoreCase(item.getName())) firewood = item;
        }

        if (coal != null && player.getInventory().getItemQuantity(coal) > 0) {
            player.getInventory().useItem(coal, 1);
            System.out.println("Pakai 1 Coal.");
            fuelConsumed = true;
        } else if (firewood != null && player.getInventory().getItemQuantity(firewood) > 0) {
            player.getInventory().useItem(firewood, 1);
            System.out.println("Pakai 1 Firewood.");
            fuelConsumed = true;
        }
        
        if (!farm.startNewCookingProcess(recipeToCook, player)) {
            System.out.println("Gagal memulai memasak, kompor sedang digunakan.");
        }

        if (!fuelConsumed) {
             player.setEnergy(player.getEnergy() + 10);
             return;
        }

        Food cookedFood = recipeToCook.getResultItem();
        player.obtainItem(cookedFood, 1);

        System.out.println(player.getName() + " memasak " + cookedFood.getName() + "!");
        System.out.println("Butuh " + recipeToCook.getTimeToCookMinutes() + " menit. Fyuh. Energi: " + player.getEnergy());
    }
}
