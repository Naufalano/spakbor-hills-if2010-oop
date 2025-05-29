package com.spakbor.cls.items;

import java.io.Serializable;
import java.util.Objects;

public abstract class Item implements Serializable {
    private static final long serialVersionUID = 1L;

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name); // Compare based on name
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, getClass());
    }
}
