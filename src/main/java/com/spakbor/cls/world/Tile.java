package com.spakbor.cls.world;
import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.spakbor.cls.core.NPC;
import com.spakbor.cls.items.Item;
import com.spakbor.enums.TileState;

public class Tile implements Serializable{
    private static final long serialVersionUID = 1L;
    private int x;
    private int y;
    private boolean isOccupied;
    private Object objectOnTile;
    private TileState state;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.isOccupied = false;
        this.objectOnTile = null;
        this.state = TileState.DEFAULT;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public Object getObjectOnTile() {
        return objectOnTile;
    }

    public void setObjectOnTile(Object objectOnTile) {
        if (objectOnTile != null && !(objectOnTile instanceof Serializable)) {
        throw new IllegalArgumentException("Object on tile must be Serializable");
        }
        this.objectOnTile = objectOnTile;
        this.setOccupied(objectOnTile != null);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TileState getState() { 
        return state;
    }

    public void setState(TileState state) { 
        this.state = state;
    }

    public boolean convertObjectOnTile(Gson gsonInstance) {
        if (this.objectOnTile instanceof com.google.gson.internal.LinkedTreeMap) {
            java.util.Map<?, ?> map = (java.util.Map<?, ?>) this.objectOnTile;

            JsonObject jsonObject = gsonInstance.toJsonTree(map).getAsJsonObject();
            Object originalObjectBeforeConversion = this.objectOnTile;
            
            if (jsonObject.has("name") && jsonObject.has("heartPoints") && jsonObject.has("relationshipStatus")) {
                try {
                    this.objectOnTile = gsonInstance.fromJson(jsonObject, NPC.class);
                    if (this.objectOnTile instanceof NPC) {
                    } else {
                        System.err.println("  Konversi ke NPC menghasilkan tipe tak terduga: " + (this.objectOnTile != null ? this.objectOnTile.getClass().getName() : "null") + " untuk data: " + jsonObject.toString());
                        this.objectOnTile = originalObjectBeforeConversion;
                    }
                    return true;
                } catch (JsonSyntaxException e) {
                    System.err.println("  JsonSyntaxException saat konversi NPC untuk " + jsonObject.toString() + ": " + e.getMessage());
                    this.objectOnTile = originalObjectBeforeConversion;
                }
            }
           
            if (jsonObject.has("$itemType") && jsonObject.has("name")) {
                try {
                    
                    this.objectOnTile = gsonInstance.fromJson(jsonObject, Item.class);
                     if (this.objectOnTile instanceof Item) {
                    } else {
                        System.err.println("  Konversi ke Item menghasilkan tipe tak terduga: " + (this.objectOnTile != null ? this.objectOnTile.getClass().getName() : "null") + " untuk data: " + jsonObject.toString());
                        this.objectOnTile = originalObjectBeforeConversion;
                    }
                    return true;
                } catch (JsonSyntaxException e) {
                    System.err.println("  JsonSyntaxException saat konversi Item untuk " + jsonObject.toString() + ": " + e.getMessage());
                    this.objectOnTile = originalObjectBeforeConversion;
                }
            }
            return true;
        }
        return false;
    }
}