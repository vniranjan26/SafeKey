package com.example.safekey;


public class ScreenItem {

    String Title,Description;
    int ScreenImg;

    public ScreenItem(String title, String description) {
        Title = title;
        Description = description;
    }


    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }
}
