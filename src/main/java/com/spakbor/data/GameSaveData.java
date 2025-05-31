package com.spakbor.data;

import java.io.Serializable;
import com.spakbor.cls.core.Farm;
import com.spakbor.cls.core.Player;

public class GameSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Player player;
    private Farm farm;

    public GameSaveData(Player player, Farm farm) {
        this.player = player;
        this.farm = farm;
    }

    public Player getPlayer() {
        return player;
    }

    public Farm getFarm() {
        return farm;
    }
}
