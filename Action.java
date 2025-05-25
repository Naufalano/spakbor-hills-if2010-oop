public abstract class Action {
    // Energy and time costs are often dynamic or specified per action type,
    // so they might be better handled within each concrete action's logic
    // or defined as constants there.

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

    // We can remove getEnergyCost and getTimeCost from the abstract class
    // if they are too specific for each action's varying calculation.
    // Each action will manage its own costs internally during execute/validate.
}