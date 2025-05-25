enum Direction { UP, DOWN, LEFT, RIGHT }

public class MovingAction extends Action {
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
            System.out.println("Validasi Gagal: Tidak ada peta aktif untuk bergerak.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        GameMap currentActiveMap = farm.getCurrentMap();
        FarmMap house = farm.getFarmMap();
        if (currentActiveMap == null) {
            System.err.println("Kesalahan: Tidak ada peta aktif saat mencoba bergerak.");
            return;
        }

        int mapWidth = currentActiveMap.getWidth();
        int mapHeight = currentActiveMap.getHeight();

        int currentX = player.getX();
        int currentY = player.getY();
        int newX = currentX;
        int newY = currentY;

        boolean movedThisAction = false;

        for (int i = 0; i < steps; i++) {
            int prevStepX = newX;
            int prevStepY = newY;

            int attemptedNextX = newX;
            int attemptedNextY = newY;

            switch (direction) {
                case UP:    attemptedNextY--; break;
                case DOWN:  attemptedNextY++; break;
                case LEFT:  attemptedNextX--; break;
                case RIGHT: attemptedNextX++; break;
            }

            if (attemptedNextX < 0 || attemptedNextX >= mapWidth ||
                attemptedNextY < 0 || attemptedNextY >= mapHeight) {

                String destinationMapName = currentActiveMap.getExitDestination(attemptedNextX, attemptedNextY);

                if (destinationMapName != null) {
                    System.out.println(player.getName() + " mencapai tepi " + currentActiveMap.getMapName() + " menuju " + destinationMapName + "...");
                    if (player.getEnergy() >= VISIT_ENERGY_COST) {
                        player.setEnergy(player.getEnergy() - VISIT_ENERGY_COST);
                        farm.advanceGameTime(VISIT_TIME_COST_MINUTES);

                        String oldLocationName = player.getCurrentLocationName();
                        farm.loadMap(destinationMapName, oldLocationName);
                    } else {
                        System.out.println("...tapi tidak punya cukup energi (" + VISIT_ENERGY_COST + ") untuk melanjutkan perjalanan.");
                        player.setLocation(prevStepX, prevStepY);
                    }
                    return;
                } else {
                    System.out.println("Tidak bisa bergerak lebih jauh: Tepi peta " + currentActiveMap.getMapName() + ".");
                    break;
                }
            } else if (attemptedNextX == house.getHouseEntranceX() && attemptedNextY == house.getHouseEntranceY()) {
                String oldLocationName = player.getCurrentLocationName();
                farm.loadMap("Player's House", oldLocationName);
            }

            Tile targetTile = currentActiveMap.getTileAtPosition(attemptedNextX, attemptedNextY);
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
                newX = attemptedNextX;
                newY = attemptedNextY;
                movedThisAction = true;
            } else {
                String reason = "jalur terhalang";
                if (targetTile != null && targetTile.getObjectOnTile() != null) {
                    Object occupant = targetTile.getObjectOnTile();
                    if (occupant instanceof String) {
                        reason += " oleh " + occupant;
                    } else {
                        reason += " oleh " + occupant.getClass().getSimpleName();
                    }
                } else if (targetTile == null) {
                    reason = "tile tujuan tidak ada (di luar batas)";
                }
                System.out.println("Tidak bisa bergerak lebih jauh ke arah itu. " + reason + " di (" + attemptedNextX + "," + attemptedNextY + ").");
                break; 
            }
        }

        if (movedThisAction) {
            player.setLocation(newX, newY); 
            // System.out.println(player.getName() + " bergerak ke (" + newX + "," + newY + ") di " + player.getCurrentLocationName() + ".");
            // currentActiveMap.display(player);
        } else if (steps > 0 && currentX == newX && currentY == newY) {
            // System.out.println(player.getName() + " tidak bisa bergerak dari (" + currentX + "," + currentY + ").");
        }
    }
}
