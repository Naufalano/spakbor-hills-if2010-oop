package com.spakbor.cls.core;
import com.spakbor.cls.items.*;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class Inventory implements Serializable{
    private static final long serialVersionUID = 1L;
    private Map<Item, Integer> inventory = new HashMap<>(); 

    public Inventory playerInv(){
        return this;
    }

    public void addItem(Item item, int amt){
        if(inventory.containsKey(item)){
            if(item instanceof Equipment){
                return;
            }
            int curAmt = inventory.get(item);
            inventory.put(item, curAmt + amt);
            return;
        }
        inventory.put(item, amt);
    }

    public void useItem(Item item, int amt){
        if(!inventory.containsKey(item)){
            System.out.println("Item tidak ada di inventory!");
            return;
        }
        int curAmt = inventory.get(item);
        if(curAmt > amt){
            inventory.put(item, curAmt - amt);
        } else if(curAmt == amt){
            inventory.remove(item);
        } else {
            System.out.println("Jumlah item tidak cukup!");
        }
    }

    public void useEquipment(Item item){
        if(item instanceof Equipment){
            item.use();
        }
    }

    public boolean hasItem(Item item){
        return inventory.containsKey(item);
    }

    public int getItemQuantity(Item item) {
        return inventory.getOrDefault(item, 0);
    }

    public Map<Item, Integer> getInventoryMap() {
        return new HashMap<>(this.inventory);
    }

    public Item getItemByName(String name) {
        for (Item item : this.inventory.keySet()) {
            if (item.getName().equalsIgnoreCase(name) && this.inventory.get(item) > 0) {
                return item;
            }
        }
        return null;
    }
}