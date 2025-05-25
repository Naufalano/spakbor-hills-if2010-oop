import java.util.Objects;

public abstract class Item {
    protected String name;

    public Item(String name) {
        this.name = name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract int getSellPrice();
    public abstract void use();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Same object instance
        // Ensure it's an Item, and not a subclass with different equality rules
        // or that subclasses properly call super.equals() if they extend equality.
        // For now, let's assume we are comparing items of the exact same class.
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name); // Compare based on name
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, getClass()); // Hash based on name and class
    }
}
