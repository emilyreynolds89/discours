package com.codepath.fbu_newsfeed.Helpers;

import com.codepath.fbu_newsfeed.Models.Source;

public class JSoupResult {
    String titleUrl;
    String imageUrl;
    String description;
    String sourceName;
    String sourceUrl;

    public JSoupResult() {
        super();
    }

    public JSoupResult(String titleUrl, String imageUrl, String description, String source) {
        this.titleUrl = titleUrl;
        this.imageUrl = imageUrl;
        this.description = description;
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

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
