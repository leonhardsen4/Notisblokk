package com.leonhardsen.notisblokk.model;

public class Tags {

    private int id;
    private String tag;

    public Tags() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String toString() {
        return tag;
    }

}
