package com.spakbor.data;
import com.spakbor.cls.items.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.spakbor.enums.SeasonType;

public class GameRegistry<T extends Item, K> {
    private static final Map<Object, Item> PROTOTYPES_BY_KEY = new HashMap<>();
    private static final List<Item> ALL_PROTOTYPES_LIST = new ArrayList<>();

    /**
     * Metode privat untuk menambahkan prototipe ke registry.
     * Dipanggil dari dalam blok statis untuk setiap item yang didefinisikan.
     *
     * @param key Kunci unik untuk item ini (misalnya, nama item).
     * @param itemPrototype Prototipe objek item.
     */
    private static synchronized void addPrototype(Object key, Item itemPrototype) {
        if (key == null || itemPrototype == null) {
            System.err.println("Kesalahan Registry: Kunci atau prototipe item tidak boleh null.");
            return;
        }
        if (PROTOTYPES_BY_KEY.containsKey(key)) {
            System.err.println("Peringatan Registry: Kunci '" + key + "' sudah ada. Menimpa item: " + PROTOTYPES_BY_KEY.get(key).getName() + " dengan " + itemPrototype.getName());
        }
        PROTOTYPES_BY_KEY.put(key, itemPrototype);
        // Tambahkan ke list hanya jika belum ada (berdasarkan referensi objek atau equals jika di-override dengan baik)
        if (!ALL_PROTOTYPES_LIST.contains(itemPrototype)) {
            ALL_PROTOTYPES_LIST.add(itemPrototype);
        }
    }

    /**
     * Contoh blok statis di mana Anda akan mendefinisikan semua data.
     * Ini akan berada di dalam kelas registry konkret Anda (misalnya, SeedDataRegistry).
     */
    /*
    static {
        // Contoh untuk SeedDataRegistry
        addPrototype("Parsnip Seeds", new Seeds("Parsnip Seeds", "Spring", 1, 20));
        addPrototype("Cauliflower Seeds", new Seeds("Cauliflower Seeds", "Spring", 5, 80));
        // ... tambahkan semua seed lainnya ...

        // Contoh untuk CropDataRegistry
        // addPrototype("Parsnip", new Crop("Parsnip", 50, 35, 3, 1));
        // ... tambahkan semua crop lainnya ...
    }
    */

    /**
     * Mengambil prototipe item berdasarkan kuncinya (misalnya, nama).
     * Mengembalikan salinan atau referensi ke prototipe tergantung kebutuhan.
     * Untuk item data game, mengembalikan referensi ke prototipe biasanya cukup
     * jika prototipe tersebut immutable atau state-nya tidak diubah.
     * Jika item bisa memiliki state unik per instance di inventaris (selain kuantitas),
     * maka metode clone() atau konstruktor salinan diperlukan di sini.
     *
     * @param key Kunci item yang dicari.
     * @return Prototipe item jika ditemukan, jika tidak Optional.empty() atau null.
     */
    // @SuppressWarnings("unchecked") // Hati-hati dengan unchecked cast jika T tidak selalu Item
    public static <T extends Item> Optional<T> getPrototypeByKey(Object key) {
        Item item = PROTOTYPES_BY_KEY.get(key);
        if (item != null) {
            try {
                return Optional.of((T) item);
            } catch (ClassCastException e) {
                System.err.println("Kesalahan Registry: Tipe item tidak cocok untuk kunci '" + key + "'. Diminta: " +
                                   "T (generic)" + ", Ditemukan: " + item.getClass().getName());
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    /**
     * Mengambil prototipe item berdasarkan namanya (umum untuk kebanyakan item game).
     * Ini adalah implementasi spesifik dari getPrototypeByKey jika kuncinya adalah nama.
     *
     * @param name Nama item.
     * @return Prototipe item jika ditemukan.
     */
    public static <T extends Item> Optional<T> getPrototypeByName(String name) {
        return getPrototypeByKey(name);
    }


    /**
     * Mendapatkan daftar semua prototipe item yang terdaftar.
     * Mengembalikan List yang tidak dapat dimodifikasi untuk mencegah perubahan eksternal.
     *
     * @return List<Item> yang berisi semua prototipe item.
     */
    public static <T extends Item> List<T> getAllPrototypes() {
        // Perlu cara untuk memfilter berdasarkan T jika ALL_PROTOTYPES_LIST berisi berbagai jenis Item.
        // Untuk registry spesifik (misalnya, SeedDataRegistry hanya berisi Seed), ini lebih sederhana.
        // Jika ini adalah registry generik yang menampung SEMUA item, maka pemfilteran diperlukan.
        // Untuk template ini, kita asumsikan satu instance GenericDataRegistry per tipe Item.
        // Jadi, ALL_PROTOTYPES_LIST di sini akan berisi item bertipe T.

        List<T> typedList = new ArrayList<>();
        for (Item item : ALL_PROTOTYPES_LIST) {
            try {
                typedList.add((T) item);
            } catch (ClassCastException e) {
                // Abaikan item yang bukan tipe T, atau log jika ini tidak diharapkan
            }
        }
        return Collections.unmodifiableList(typedList);
    }

    /**
     * Contoh penggunaan dalam registry konkret:
     */
    public static class SeedDataRegistry {
        private static final Map<String, Seeds> SEED_PROTOTYPES = new HashMap<>();
        private static final List<Seeds> ALL_SEED_PROTOTYPES_LIST = new ArrayList<>();

        private static synchronized void addSeed(Seeds seedPrototype) {
            if (seedPrototype == null || seedPrototype.getName() == null) {
                System.err.println("Kesalahan SeedRegistry: Prototipe seed atau namanya tidak boleh null.");
                return;
            }
            if (SEED_PROTOTYPES.containsKey(seedPrototype.getName())) {
                System.err.println("Peringatan SeedRegistry: Nama seed '" + seedPrototype.getName() + "' sudah ada.");
            }
            SEED_PROTOTYPES.put(seedPrototype.getName(), seedPrototype);
            if (!ALL_SEED_PROTOTYPES_LIST.contains(seedPrototype)) {
                 ALL_SEED_PROTOTYPES_LIST.add(seedPrototype);
            }
        }

        static {
        }

        public static Optional<Seeds> getSeedByName(String name) {
            return Optional.ofNullable(SEED_PROTOTYPES.get(name));
        }

        public static List<Seeds> getAllSeeds() {
            return Collections.unmodifiableList(new ArrayList<>(ALL_SEED_PROTOTYPES_LIST));
        }

        public static List<Seeds> getSeedsForSeason(SeasonType season) {
            List<Seeds> result = new ArrayList<>();
            for (Seeds seed : ALL_SEED_PROTOTYPES_LIST) {
                if (seed.canBePlantedIn(season)) {
                    result.add(seed);
                }
            }
            return Collections.unmodifiableList(result);
        }
    }
}