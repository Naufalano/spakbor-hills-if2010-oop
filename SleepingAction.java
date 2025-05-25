public class SleepingAction extends Action {
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
            System.out.println("Validasi Gagal: Anda hanya bisa tidur (manual) di dalam rumah Anda.");
            return false;
        }

        String adjacentObject = InteractionHelper.getAdjacentInteractableObject(player, farm.getCurrentMap());
        if (!PlayerHouseMap.BED_ID.equals(adjacentObject)) {
            System.out.println("Validasi Gagal: Anda harus berada di sebelah kasur untuk tidur (manual).");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        if (isAutomaticSleep) {
            System.out.println(player.getName() + " pingsan karena kelelahan dan tidur hingga pagi...");
        } else {
            System.out.println(player.getName() + " pergi tidur...");
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

        System.out.println(player.getName() + " is sleeping...");
        farm.nextDay();

        System.out.println("Good morning! Energy is now " + player.getEnergy() + "/" + Player.MAX_ENERGY);
        System.out.println("It is now Day " + farm.getCurrentDayInSeason() + " of " + farm.getCurrentSeason().toString() +
                           ", Weather: " + farm.getCurrentWeather().toString());
        farm.setCurrentlySleeping(false);
    }
}