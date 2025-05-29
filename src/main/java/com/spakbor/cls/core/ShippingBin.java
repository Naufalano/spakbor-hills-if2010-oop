package com.spakbor.cls.core;
import com.spakbor.cls.items.*;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class ShippingBin implements Serializable{
    private static final long serialVersionUID = 1L;
    private List<Item> items;
    private final int MAX_SLOTS = 16;

    public ShippingBin() {
        items = new ArrayList<>();
    }

    public boolean addItem(Item item) {
        if (items.size() < MAX_SLOTS) {
            items.add(item);
            return true;
        }
        return false; 
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public boolean hasItem(Item item) {
        return items.contains(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public int getMaxSlots() {
        return MAX_SLOTS;
    }

    public int getMaxCapacity() {
        return MAX_SLOTS;
    }

    public int getCurrentSize() {
        return items.size();
    }
}
