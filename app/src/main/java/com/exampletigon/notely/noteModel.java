package com.exampletigon.notely;

public class noteModel {
    private String title;
    private String content;
    private String DMY;




    public noteModel() {
        // Required no-argument constructor for Firebase
    }


    public noteModel(String title, String content, String DMY) {
        this.title = title;
        this.content = content;
        this.DMY = DMY;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDMY() {
        return DMY;
    }

    public void setDMY(String DMY) {
        this.DMY = DMY;
    }
}
