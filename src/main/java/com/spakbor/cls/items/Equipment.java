package com.spakbor.cls.items;

public class Equipment extends Item {
    private static final long serialVersionUID = 1L;

    public Equipment(String name) {
        super(name);
    }

    @Override
    public int getSellPrice() {
        return 0; // Bisa dikustomisasi jika suatu equipment bisa dijual
    }

    @Override
    public void use() {}
}
