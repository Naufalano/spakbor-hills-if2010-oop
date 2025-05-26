package action;
import cls.core.*;

public abstract class Action {
    /**
     * Executes the action.
     * @param player The player performing the action.
     * @param farm The farm context, providing access to map, time, etc.
     */
    public abstract void execute(Player player, Farm farm);

    /**
     * Validates if the action can be performed.
     * @param player The player performing the action.
     * @param farm The farm context.
     * @return true if the action is valid, false otherwise.
     */
    public abstract boolean validate(Player player, Farm farm);
}