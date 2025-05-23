package spakbor.items;

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
}
