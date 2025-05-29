package com.spakbor.action;
import com.spakbor.cls.core.Farm;
import com.spakbor.cls.core.PlantedCrop;
import com.spakbor.cls.core.Player;
import com.spakbor.cls.world.FarmMap;
import com.spakbor.cls.world.GameMap;
import com.spakbor.cls.world.GenericInteriorMap;
import com.spakbor.cls.world.PlayerHouseMap;
import com.spakbor.cls.world.StoreMap;
import com.spakbor.cls.world.Tile;
import com.spakbor.cls.world.TownMap;
import com.spakbor.enums.Direction;

public class MovingAction implements Action {
    private static final long serialVersionUID = 1L;

    private Direction direction;
    private int steps;

    private static final int VISIT_ENERGY_COST = 10;
    private static final int VISIT_TIME_COST_MINUTES = 15;

    public MovingAction(Direction direction, int steps) {
        this.direction = direction;
        this.steps = Math.max(1, steps);
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (farm.getCurrentMap() == null) {
            System.out.println("Tidak ada peta aktif untuk bergerak.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        GameMap initialMap = farm.getCurrentMap(); 
        if (initialMap == null) {
            System.err.println("Kesalahan: Peta saat ini null di MovingAction.execute.");
            return;
        }

        int currentX = player.getX();
        int currentY = player.getY();
        
        int finalXOnThisMap = currentX;
        int finalYOnThisMap = currentY;

        for (int i = 0; i < steps; i++) {
            GameMap activeMapForStep = farm.getCurrentMap(); 
            if (activeMapForStep == null) {
                System.err.println("Kesalahan: Peta aktif menjadi null di tengah langkah.");
                player.setLocation(finalXOnThisMap, finalYOnThisMap);
                return;
            }
            
            int mapWidth = activeMapForStep.getWidth();
            int mapHeight = activeMapForStep.getHeight();

            int attemptedNextX = finalXOnThisMap; 
            int attemptedNextY = finalYOnThisMap;

            switch (direction) {
                case UP:    attemptedNextY--; break;
                case DOWN:  attemptedNextY++; break;
                case LEFT:  attemptedNextX--; break;
                case RIGHT: attemptedNextX++; break;
            }

            String destinationMapName = null;
            boolean isTransitionViaEdge = false;

            if (attemptedNextX < 0 || attemptedNextX >= mapWidth ||
                attemptedNextY < 0 || attemptedNextY >= mapHeight) {
                destinationMapName = activeMapForStep.getExitDestination(attemptedNextX, attemptedNextY);
                isTransitionViaEdge = true;
            }
            else if (activeMapForStep instanceof FarmMap) {
                if (attemptedNextX == ((FarmMap) activeMapForStep).getHouseEntranceX() && attemptedNextY == ((FarmMap) activeMapForStep).getHouseEntranceY()) {
                    destinationMapName = "Player's House";
                }
            }
            else {
                Tile targetTileForDoorCheck = activeMapForStep.getTileAtPosition(attemptedNextX, attemptedNextY);
                if (targetTileForDoorCheck != null && targetTileForDoorCheck.getObjectOnTile() instanceof String) {
                    String objectIdOnTarget = (String) targetTileForDoorCheck.getObjectOnTile();
                    
                    if (activeMapForStep instanceof TownMap) {
                        TownMap.DoorInfo doorInfo = ((TownMap) activeMapForStep).getDoorInfo(objectIdOnTarget);
                        if (doorInfo != null && doorInfo.x == attemptedNextX && doorInfo.y == attemptedNextY) {
                            destinationMapName = doorInfo.destinationMapName;
                        }
                    } else if (activeMapForStep instanceof PlayerHouseMap && PlayerHouseMap.DOOR_TO_FARM_ID.equals(objectIdOnTarget)) {
                        destinationMapName = "Farm";
                    } else if (activeMapForStep instanceof StoreMap && StoreMap.DOOR_ID.equals(objectIdOnTarget)) {
                        destinationMapName = "Town";
                    } else if (activeMapForStep instanceof GenericInteriorMap && GenericInteriorMap.DOOR_ID.equals(objectIdOnTarget)) {
                        destinationMapName = "Town";
                    }
                }
            }

            if (destinationMapName != null) {
                String transitionType = isTransitionViaEdge ? "tepi peta" : "pintu";
                System.out.println(player.getName() + " mencapai " + transitionType + " di " + activeMapForStep.getMapName() + " menuju " + destinationMapName + "...");
                
                if (player.getEnergy() + 20 >= VISIT_ENERGY_COST && !destinationMapName.equals("Player's House") && !player.getCurrentLocationName().equals("Player's House") && !(activeMapForStep instanceof GenericInteriorMap)) {
                    player.setEnergy(player.getEnergy() - VISIT_ENERGY_COST);
                    farm.advanceGameTime(VISIT_TIME_COST_MINUTES);
                    String oldLocationName = player.getCurrentLocationName();
                    farm.loadMap(destinationMapName, oldLocationName); 
                    return; 
                } else if (activeMapForStep instanceof GenericInteriorMap) {
                    String oldLocationName = player.getCurrentLocationName();
                    farm.loadMap(destinationMapName, oldLocationName); 
                    return;
                } else if (destinationMapName.equals("Player's House")) {
                    String oldLocationName = player.getCurrentLocationName();
                    farm.loadMap(destinationMapName, oldLocationName); 
                    return;
                } else if (player.getCurrentLocationName().equals("Player's House")) {
                    farm.loadMap("Farm", player.getCurrentLocationName());
                    return;
                } else {
                    System.out.println("...tapi tidak punya cukup energi (" + VISIT_ENERGY_COST + ") untuk melanjutkan.");
                    farm.scheduleAutomaticSleep();
                    break;
                }
            }

            Tile targetTile = activeMapForStep.getTileAtPosition(attemptedNextX, attemptedNextY);
            boolean canMoveToTarget = false;
            if (targetTile != null) {
                if (!targetTile.isOccupied()) {
                    canMoveToTarget = true;
                } else {
                    Object occupant = targetTile.getObjectOnTile();
                    if (occupant instanceof PlantedCrop) {
                        canMoveToTarget = true;
                    }
                }
            }

            if (canMoveToTarget) {
                finalXOnThisMap = attemptedNextX;
                finalYOnThisMap = attemptedNextY;
            } else {
                String reason = "jalur terhalang";
                if (targetTile != null && targetTile.getObjectOnTile() != null) {
                    Object occupant = targetTile.getObjectOnTile();
                    if (occupant instanceof String) reason += " oleh " + occupant;
                    else reason += " oleh " + occupant.getClass().getSimpleName();
                }
                System.out.println("Tidak bisa bergerak lebih jauh ke arah itu. " + reason + " di (" + attemptedNextX + "," + attemptedNextY + ").");
                break;
            }
        }

        if (finalXOnThisMap != currentX || finalYOnThisMap != currentY) {
            player.setLocation(finalXOnThisMap, finalYOnThisMap);
        }
    }
}
