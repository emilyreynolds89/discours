package com.codepath.fbu_newsfeed;

public class JSoupResult {
    String titleUrl;
    String factUrl;
    String imageUrl;
    int biasUrl;

    public JSoupResult() {
        super();
    }

    public JSoupResult(String titleUrl, String factUrl, String imageUrl, int biasUrl) {
        this.titleUrl = titleUrl;
        this.factUrl = factUrl;
        this.imageUrl = imageUrl;
        this.biasUrl = biasUrl;
    }

    public String getTitleUrl() {
        return titleUrl;
    }

    public void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
    }

    public String getFactUrl() {
        return factUrl;
    }

    public void setFactUrl(String factUrl) {
        this.factUrl = factUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getBiasUrl() {
        return biasUrl;
    }

    public void setBiasUrl(int biasUrl) {
        this.biasUrl = biasUrl;
    }
}
