package com.codepath.fbu_newsfeed;

public class Tag {

    Title title;

    public Tag(Title t) {
        title = t;
    }

    enum Title  {
        WORLD, US, POLITICS, BUSINESS, OPINION, TECH, SCIENCE, HEALTH, SPORTS, ARTS, BOOKS, STYLE, FOOD, TRVAEL;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title t) {
        title = t;
    }
}
