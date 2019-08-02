package com.codepath.fbu_newsfeed.Models;

public class Tag {

    private Title title;

    public Tag(Title title) {
        this.title = title;
    }

    enum Title  {
        UNTAGGED, WORLD, US, POLITICS, BUSINESS, OPINION, TECH, SCIENCE, HEALTH, SPORTS, ARTS, BOOKS, STYLE, FOOD, TRAVEL, LIFE

    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }
}
