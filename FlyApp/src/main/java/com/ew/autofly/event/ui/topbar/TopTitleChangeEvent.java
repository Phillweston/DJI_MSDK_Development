package com.ew.autofly.event.ui.topbar;



public class TopTitleChangeEvent {

   private String title;

    public TopTitleChangeEvent(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
