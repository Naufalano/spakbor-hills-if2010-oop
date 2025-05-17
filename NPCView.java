public class NPCView {
    private String action;

    public NPCView(String action){
        this.action = action;
    }

    public void setCommand(String command) {
        this.action = command;
    }

    public String getAction() {
        return this.action;
    }

    public void showAction() {
        System.out.println(action);
    }
}
