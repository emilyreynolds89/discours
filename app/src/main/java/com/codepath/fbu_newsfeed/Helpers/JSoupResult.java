package com.codepath.fbu_newsfeed.Helpers;

public class JSoupResult {
    String titleUrl;
    String imageUrl;
    String description;
    String source;

    public JSoupResult() {
        super();
    }

    public JSoupResult(String titleUrl, String imageUrl, String description, String source) {
        this.titleUrl = titleUrl;
        this.imageUrl = imageUrl;
        this.description = description;
        this.source = source;
    }

    public String getTitleUrl() {
        return titleUrl;
    }

    public void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
