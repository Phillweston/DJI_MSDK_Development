package com.ew.autofly.module.setting.event;


public class EnterSecondMenuEvent {

    private String title;

    public EnterSecondMenuEvent(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
