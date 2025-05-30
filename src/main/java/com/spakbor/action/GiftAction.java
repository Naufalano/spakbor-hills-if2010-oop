package com.spakbor.action;
import com.spakbor.cls.core.*;
import com.spakbor.cls.items.*;

public class GiftAction implements Action {
    private static final long serialVersionUID = 1L;
    private NPC targetNpc;
    private Item giftItem;
    private int quantity;

    private static final int ENERGY_COST = 5;
    private static final int TIME_COST_MINUTES = 10;

    public GiftAction(NPC targetNpc, Item giftItem, int quantity) {
        this.targetNpc = targetNpc;
        this.giftItem = giftItem;
        this.quantity = quantity;
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (targetNpc == null || giftItem == null) {
            System.out.println("NPC target atau item hadiah tidak valid.");
            return false;
        }
        if (player.getEnergy() + 20 < ENERGY_COST) {
            System.out.println("Energi tidak cukup untuk memberi hadiah (-" + ENERGY_COST + ").");
            return false;
        }
        if (player.getInventory().getItemQuantity(giftItem) < quantity) {
            System.out.println("Tidak memiliki " + giftItem.getName() + ".");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        player.setEnergy(player.getEnergy() - ENERGY_COST);
        farm.advanceGameTime(TIME_COST_MINUTES);

        int oldAffection = targetNpc.getAffection();
        targetNpc.giftCheck(giftItem);
        player.getInventory().useItem(giftItem, quantity);
        player.recordGiftToNPC(targetNpc.getName());

        System.out.println("Memberikan " + giftItem.getName() + " kepada " + targetNpc.getName() + ".");
        System.out.println("Rasa suka " + targetNpc.getName() + " berubah dari " + oldAffection + " menjadi " + targetNpc.getAffection() + ".");
        System.out.println("Energi tersisa: " + player.getEnergy());
    }
}