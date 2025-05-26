package main;

import item.*;

public class AssetSetter {
    GamePanel gp;


    public AssetSetter(GamePanel gp){
        this.gp = gp;
    }

    public void setItem(){
        gp.items[0] = new ItemKey(gp);
        gp.items[0].WorldX = 23 * gp.tileSize;
        gp.items[0].WorldY = 7 * gp.tileSize;

        gp.items[1] = new ItemKey(gp);
        gp.items[1].WorldX = 23 * gp.tileSize;
        gp.items[1].WorldY = 40 * gp.tileSize;

        gp.items[2] = new ItemKey(gp);
        gp.items[2].WorldX = 37 * gp.tileSize;
        gp.items[2].WorldY = 7 * gp.tileSize;

        gp.items[3] = new ItemDoor(gp);
        gp.items[3].WorldX = 10 * gp.tileSize;
        gp.items[3].WorldY = 11 * gp.tileSize;

        gp.items[4] = new ItemDoor(gp);
        gp.items[4].WorldX = 8 * gp.tileSize;
        gp.items[4].WorldY = 28 * gp.tileSize;

        gp.items[5] = new ItemChest(gp);
        gp.items[5].WorldX = 10 * gp.tileSize;
        gp.items[5].WorldY = 6 * gp.tileSize;

        gp.items[6] = new ItemBoots(gp);
        gp.items[6].WorldX = 37 * gp.tileSize;
        gp.items[6].WorldY = 42 * gp.tileSize;
        
    }
}
