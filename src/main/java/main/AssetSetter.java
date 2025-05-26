package main;

import item.ItemKey;

public class AssetSetter {
    GamePanel gp;


    public AssetSetter(GamePanel gp){
        this.gp = gp;
    }

    public void setItem(){
        gp.items[0] = new ItemKey();
        gp.items[0].WorldX = 23 * gp.tileSize;
        gp.items[0].WorldY = 7 * gp.tileSize;

        gp.items[1] = new ItemKey();
        gp.items[1].WorldX = 23 * gp.tileSize;
        gp.items[1].WorldY = 40 * gp.tileSize;
    }
}
