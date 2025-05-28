package com.spakbor.action;

import com.spakbor.cls.core.*;
import java.io.Serializable;

public interface Action extends Serializable {
    /**
     * Executes the action.
     * @param player The player performing the action.
     * @param farm The farm context, providing access to map, time, etc.
     */
    void execute(Player player, Farm farm);

    /**
     * Validates if the action can be performed.
     * @param player The player performing the action.
     * @param farm The farm context.
     * @return true if the action is valid, false otherwise.
     */
    boolean validate(Player player, Farm farm);
}
