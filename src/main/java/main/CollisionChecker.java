package main;

import entity.Entity;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp){
        this.gp = gp;
    }

    public void checkTile(Entity entity){
        int entityLeftWorldX = entity.WorldX + entity.solidArea.x;
        int entityRightWorldX = entity.WorldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.WorldY + entity.solidArea.y;
        int entityBottomWorldY = entity.WorldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileNum1, tileNum2;

        switch(entity.direction) {
        case "up":
            entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
            tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
            tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
            if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                entity.collisionOn = true;
            }
            break;
        case "down":
            entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
            tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
            tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
            if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                entity.collisionOn = true;
            }
            break;
        case "left":
            entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
            tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
            tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
            if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                entity.collisionOn = true;
            }
            break;
        case "right":
            entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
            tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
            tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
            if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
                entity.collisionOn = true;
            }
            break;
        }
    }
    public int checkObject(Entity entity, boolean player){
        int index = 999;
        for(int i = 0; i < gp.items.length; i++){
            if(gp.items[i] != null){
                entity.solidArea.x = entity.WorldX + entity.solidArea.x;
                entity.solidArea.y = entity.WorldY + entity.solidArea.y;

                gp.items[i].solidArea.x = gp.items[i].WorldX + gp.items[i].solidArea.x;
                gp.items[i].solidArea.y = gp.items[i].WorldY + gp.items[i].solidArea.y;

                switch (entity.direction) {
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        if(entity.solidArea.intersects(gp.items[i].solidArea)){
                            if(gp.items[i].collision == true){
                                entity.collisionOn = true;
                            }
                            if(player == true){
                            index = i;
                        }
                        }
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        if(entity.solidArea.intersects(gp.items[i].solidArea)){
                            if(gp.items[i].collision == true){
                                entity.collisionOn = true;
                            }
                            if(player == true){
                            index = i;
                        }
                        }
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        if(entity.solidArea.intersects(gp.items[i].solidArea)){
                            if(gp.items[i].collision == true){
                                entity.collisionOn = true;
                            }
                            if(player == true){
                            index = i;
                        }
                        }
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        if(entity.solidArea.intersects(gp.items[i].solidArea)){
                            if(gp.items[i].collision == true){
                                entity.collisionOn = true;
                            }
                            if(player == true){
                            index = i;
                        }
                        }
                        break;
                }
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.items[i].solidArea.x = gp.items[i].solidAreaDefaultX;
                gp.items[i].solidArea.y = gp.items[i].solidAreaDefaultY;
            }
        }

        return index;
    }

    public int checkNPC(Entity entity, Entity[] target){
        int index = 999;
        for(int i = 0; i < target.length; i++){
            if(target[i] != null && target[i] != entity){
                entity.solidArea.x = entity.WorldX + entity.solidArea.x;
                entity.solidArea.y = entity.WorldY + entity.solidArea.y;

                target[i].solidArea.x = target[i].WorldX + target[i].solidArea.x;
                target[i].solidArea.y = target[i].WorldY + target[i].solidArea.y;

                switch (entity.direction) {
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        if(entity.solidArea.intersects(target[i].solidArea)){
                                entity.collisionOn = true;
                                index = i;
                        }
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        if(entity.solidArea.intersects(target[i].solidArea)){
                                entity.collisionOn = true;
                                index = i;
                        }
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        if(entity.solidArea.intersects(target[i].solidArea)){
                                entity.collisionOn = true;
                            index = i;
                        }
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        if(entity.solidArea.intersects(target[i].solidArea)){
                                entity.collisionOn = true;
                            index = i;
                        }
                        break;
                }
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                target[i].solidArea.x = target[i].solidAreaDefaultX;
                target[i].solidArea.y = target[i].solidAreaDefaultY;
            }
        }

        return index;
    }

    public void checkPlayer(Entity entity){
                entity.solidArea.x = entity.WorldX + entity.solidArea.x;
                entity.solidArea.y = entity.WorldY + entity.solidArea.y;

                gp.player.solidArea.x = gp.player.WorldX + gp.player.solidArea.x;
                gp.player.solidArea.y = gp.player.WorldY + gp.player.solidArea.y;

                switch (entity.direction) {
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        if(entity.solidArea.intersects(gp.player.solidArea)){   
                                entity.collisionOn = true;
                            }
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                         if(entity.solidArea.intersects(gp.player.solidArea)){   
                                entity.collisionOn = true;
                            }
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                         if(entity.solidArea.intersects(gp.player.solidArea)){   
                                entity.collisionOn = true;
                            }
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                         if(entity.solidArea.intersects(gp.player.solidArea)){   
                                entity.collisionOn = true;
                            }
                        break;
                }
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.player.solidArea.x = gp.player.solidAreaDefaultX;
                gp.player.solidArea.y = gp.player.solidAreaDefaultY;
            }
        }
