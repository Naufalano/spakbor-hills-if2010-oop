package com.spakbor.cls.items;

public class ItemStub extends Item {
    public ItemStub(String name) {
        super(name);
    }
    @Override
    public int getSellPrice() { return 0; }
    @Override
    public void use() {}
}

