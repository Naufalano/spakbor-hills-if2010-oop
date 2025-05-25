import java.util.ArrayList;
import java.util.List;

public class ShippingBin {
    private List<Item> items;
    private final int MAX_SLOTS = 16;

    public ShippingBin() {
        items = new ArrayList<>();
    }

    // Menambah item ke ShippingBin
    public boolean addItem(Item item) {
        if (items.size() < MAX_SLOTS) {
            items.add(item);
            return true;
        }
        return false;  // Jika sudah penuh
    }

    // Menghapus item dari ShippingBin
    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    // Mengecek apakah item ada di ShippingBin
    public boolean hasItem(Item item) {
        return items.contains(item);
    }

    public List<Item> getItems() {
        return items;
    }

    // Mendapatkan kapasitas maksimal
    public int getMaxSlots() {
        return MAX_SLOTS;
    }
}
