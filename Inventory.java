import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<Item, Integer> inventory = new HashMap<>(); 

    public Inventory playerInv(){
        Item hoe = new Equipment("Hoe");
        Seeds parsnips = SeedDataRegistry.getSeedByName("Parsnip Seeds");
        if (parsnips != null) {
            this.inventory.put(parsnips, 15); // Give 5 Parsnip Seeds
        }
        Item can = new Equipment("Watering Can");
        Item pickaxe = new Equipment("Pickaxe");
        Item rod = new Equipment("Fishing Rod");
        inventory.put(hoe, 1);
        inventory.put(can, 1);
        inventory.put(pickaxe, 1);
        inventory.put(rod, 1);
        return this;
    }

    public void addItem(Item item, int amt){
        inventory.put(item, amt);
    }

    public void useItem(Item item, int amt){
        if(!inventory.containsKey(item)){
            System.out.println("Item tidak ada di inventory!");
            return;
        }
        int curAmt = inventory.get(item);
        if(curAmt > amt){
            inventory.put(item, curAmt - amt);
        } else if(curAmt == amt){
            inventory.remove(item);
        } else {
            System.out.println("Jumlah item tidak cukup!");
        }
    }

    public void useEquipment(Item item){
        if(item instanceof Equipment){
            item.use();
        }
    }

    public boolean hasItem(Item item){
        return inventory.containsKey(item);
    }

    public int getItemQuantity(Item item) {
        return inventory.getOrDefault(item, 0);
    }

    public Map<Item, Integer> getInventoryMap() {
        // Option 1: Return a copy to prevent external modification
        return new HashMap<>(this.inventory);

        // Option 2: Return an unmodifiable view (safer if you don't want copies made often)
        // return Collections.unmodifiableMap(this.inventory);
    }

    public Item getItemByName(String name) {
        for (Item item : this.inventory.keySet()) { // this.inventory is Map<Item, Integer>
            if (item.getName().equalsIgnoreCase(name) && this.inventory.get(item) > 0) {
                return item;
            }
        }
        return null;
    }
}
