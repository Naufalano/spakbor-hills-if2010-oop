package action;
import cls.core.*;
import cls.world.*;
import utils.*;

public class SleepingAction implements Action {
    private boolean isAutomaticSleep;

    /**
     * Konstruktor untuk aksi tidur manual (dipicu oleh pemain).
     */
    public SleepingAction() {
        this(false);
    }

    /**
     * Konstruktor untuk aksi tidur, bisa manual atau otomatis.
     * @param isAutomatic true jika ini adalah tidur otomatis yang dipicu sistem, false jika manual.
     */
    public SleepingAction(boolean isAutomatic) {
        this.isAutomaticSleep = isAutomatic;
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (isAutomaticSleep) {
            return true;
        }

        if (!player.getCurrentLocationName().equals("Player's House")) {
            System.out.println("Kamu hanya bisa tidur di dalam rumah.");
            return false;
        }

        String adjacentObject = InteractionHelper.getAdjacentInteractableObject(player, farm.getCurrentMap());
        if (!PlayerHouseMap.BED_ID.equals(adjacentObject)) {
            System.out.println("Harus berada di sebelah kasur untuk tidur.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        if (isAutomaticSleep) {
            System.out.println(player.getName() + " tepar ampe pagi...");
        } else {
            System.out.println(player.getName() + " turu...");
        }

        farm.setCurrentlySleeping(true);
        int currentEnergy = player.getEnergy();
        if (currentEnergy == 0) {
            player.setEnergy(10);
        } else if (currentEnergy < (Player.MAX_ENERGY * 0.10)) {
            player.setEnergy(Player.MAX_ENERGY / 2);
        } else {
            player.setEnergy(Player.MAX_ENERGY);
        }
        farm.nextDay();

        System.out.println("Wilujeng enjing! Energi " + player.getEnergy() + "/" + Player.MAX_ENERGY);
        System.out.println("Sekarang hari " + farm.getCurrentDayInSeason() + " musim " + farm.getCurrentSeason().toString() + ", Cuaca: " + farm.getCurrentWeather().toString());
        farm.setCurrentlySleeping(false);
    }
}