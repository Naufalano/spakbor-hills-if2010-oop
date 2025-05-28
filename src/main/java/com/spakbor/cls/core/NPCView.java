package com.spakbor.cls.core;
import java.io.Serializable;


public class NPCView implements Serializable{
    private static final long serialVersionUID = 1L;
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
