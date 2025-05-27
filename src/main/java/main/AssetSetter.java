package main;

import entity.NPC_Perry;
import entity.NPC_MayorTadi;
import entity.NPC_Caroline;
import entity.NPC_Dasco;
import entity.NPC_Emily;
import entity.NPC_Abigail;
import item.*;

public class AssetSetter {
    GamePanel gp;


    public AssetSetter(GamePanel gp){
        this.gp = gp;
    }

    public void setItem(){
         
    }

    public void setNPC(){
        gp.NPC[0] = new NPC_MayorTadi(gp);
        gp.NPC[0].WorldX = gp.tileSize*21;
        gp.NPC[0].WorldY = gp.tileSize*21;

        gp.NPC[1] = new NPC_Caroline(gp);
        gp.NPC[1].WorldX = gp.tileSize*23;
        gp.NPC[1].WorldY = gp.tileSize*23;

        gp.NPC[2] = new NPC_Perry(gp);
        gp.NPC[2].WorldX = gp.tileSize*22;
        gp.NPC[2].WorldY = gp.tileSize*23;

        gp.NPC[3] = new NPC_Dasco(gp);
        gp.NPC[3].WorldX = gp.tileSize*24;
        gp.NPC[3].WorldY = gp.tileSize*24;

        gp.NPC[4] = new NPC_Emily(gp);
        gp.NPC[4].WorldX = gp.tileSize*24;
        gp.NPC[4].WorldY = gp.tileSize*19;

        gp.NPC[5] = new NPC_Abigail(gp);
        gp.NPC[5].WorldX = gp.tileSize*24;
        gp.NPC[5].WorldY = gp.tileSize*23;
    }
}
