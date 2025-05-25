// Enum for directions might be useful if not already defined
enum Direction { UP, DOWN, LEFT, RIGHT }

// Diasumsikan Direction enum, Player, Farm, GameMap, Tile sudah dapat diakses

public class MovingAction extends Action {
    private Direction direction;
    private int steps;

    // Biaya untuk transisi antar peta (sebelumnya bagian dari VisitingAction atau handleAutomaticVisit)
    private static final int VISIT_ENERGY_COST = 10;
    private static final int VISIT_TIME_COST_MINUTES = 15;

    public MovingAction(Direction direction, int steps) {
        this.direction = direction;
        this.steps = Math.max(1, steps); // Minimal 1 langkah
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        // Validasi dasar untuk bergerak. Bisa ditambahkan pengecekan energi per langkah jika ada.
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
            int prevStepX = newX; // Posisi sebelum mencoba langkah ini
            int prevStepY = newY;

            int attemptedNextX = newX;
            int attemptedNextY = newY;

            switch (direction) {
                case UP:    attemptedNextY--; break;
                case DOWN:  attemptedNextY++; break;
                case LEFT:  attemptedNextX--; break;
                case RIGHT: attemptedNextX++; break;
            }

            // 1. Cek apakah pemain mencoba bergerak keluar dari batas peta saat ini
            if (attemptedNextX < 0 || attemptedNextX >= mapWidth ||
                attemptedNextY < 0 || attemptedNextY >= mapHeight) {

                // Pemain mencoba keluar dari batas. Cek apakah ini adalah titik keluar yang valid.
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
                    return; // Aksi bergerak (dan mungkin transisi) selesai untuk pemanggilan ini
                } else {
                    System.out.println("Tidak bisa bergerak lebih jauh: Tepi peta " + currentActiveMap.getMapName() + ".");
                    break;
                }
            } else if (attemptedNextX == house.getHouseEntranceX() && attemptedNextY == house.getHouseEntranceY()) {
                String oldLocationName = player.getCurrentLocationName();
                farm.loadMap("Player's House", oldLocationName);
            }

            // 2. Jika masih di dalam batas peta, cek tile tujuan
            Tile targetTile = currentActiveMap.getTileAtPosition(attemptedNextX, attemptedNextY);
            boolean canMoveToTarget = false;

            if (targetTile != null) {
                if (!targetTile.isOccupied()) {
                    canMoveToTarget = true; // Tile kosong selalu bisa dilewati
                } else {
                    Object occupant = targetTile.getObjectOnTile();
                    if (occupant instanceof PlantedCrop) {
                        canMoveToTarget = true; // Pemain BISA berjalan di atas tanaman
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
                } else if (targetTile == null) { // Seharusnya sudah ditangani oleh cek batas di atas
                    reason = "tile tujuan tidak ada (di luar batas)";
                }
                System.out.println("Tidak bisa bergerak lebih jauh ke arah itu. " + reason + " di (" + attemptedNextX + "," + attemptedNextY + ").");
                break; // Hentikan loop langkah jika jalur terhalang
            }
        }

        if (movedThisAction) {
            player.setLocation(newX, newY); // Atur posisi akhir pemain
            // Pesan pergerakan akan ditampilkan oleh GameDriver jika perlu, atau di sini jika diinginkan
            // System.out.println(player.getName() + " bergerak ke (" + newX + "," + newY + ") di " + player.getCurrentLocationName() + ".");
            // currentActiveMap.display(player); // Tampilan peta akan di-handle oleh GameDriver
        } else if (steps > 0 && currentX == newX && currentY == newY) {
            // Hanya tampilkan pesan ini jika tidak ada pergerakan sama sekali dari posisi awal
            // Pesan "jalur terhalang" atau "tepi peta" sudah muncul di dalam loop.
            // System.out.println(player.getName() + " tidak bisa bergerak dari (" + currentX + "," + currentY + ").");
        }
    }
}
