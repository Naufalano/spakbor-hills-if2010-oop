package main;

import entity.NPC_OldMan;
import item.*;

public class AssetSetter {
    GamePanel gp;


    public AssetSetter(GamePanel gp){
        this.gp = gp;
    }

    public void setItem(){
         
    }

    public void setNPC(){
        gp.NPC[0] = new NPC_OldMan(gp);
        gp.NPC[0].WorldX = gp.tileSize*21;
        gp.NPC[0].WorldY = gp.tileSize*21;
        gp.NPC[1] = new NPC_OldMan(gp);
    }
}
