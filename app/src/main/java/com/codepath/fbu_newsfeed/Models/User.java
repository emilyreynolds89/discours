package com.codepath.fbu_newsfeed.Models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.List;

@ParseClassName("_User")
public class User extends ParseUser implements Serializable {
    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_PROFILEIMAGE = "profileImage";
    public static final String KEY_BIO = "bio";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_BIAS_AVERAGE = "biasAverage";
    public static final String KEY_FACT_AVERAGE = "factAverage";
    public static final String KEY_ARTICLE_COUNT = "articleCount";

    public static final int LIMIT = 30;

    public float biasAverage;
    public float factAverage;
    public int articleCount;

    public String getUsername() {
        return getString(KEY_USERNAME);
    }

    public void setUsername(String userName) {
        put(KEY_USERNAME, userName);
    }

    public String getFullName() {
        return getString(KEY_FULLNAME);
    }

    public void setFullName(String fullName) {
        put(KEY_FULLNAME, fullName);
    }

    public ParseFile getProfileImage() {
        return getParseFile(KEY_PROFILEIMAGE);
    }

    public void setProfileImage(ParseFile file) {
        put(KEY_PROFILEIMAGE, file);
    }

    public float getBiasAverage() {
        return getInt(KEY_BIAS_AVERAGE);
    }

    public void setBiasAverage(float biasAverage) {
        this.biasAverage = biasAverage;
        put(KEY_BIAS_AVERAGE, biasAverage);
    }

    public float getFactAverage() {
        return getInt(KEY_FACT_AVERAGE);
    }

    public void setFactAverage(float factAverage) {
        this.factAverage = factAverage;
        put(KEY_FACT_AVERAGE, factAverage);
    }

    public int getArticleCount() {
        return getInt(KEY_ARTICLE_COUNT);
    }

    public void setArticleCount(int articleCount) {
        this.articleCount = articleCount;
        put(KEY_ARTICLE_COUNT, articleCount);
    }


    private int queryArticleCount(boolean queryFacts) {
        ParseQuery<Share> query = ParseQuery.getQuery("Share");
        query.whereEqualTo("user", this);
        if (queryFacts) {
            query.whereNotEqualTo(Article.KEY_TRUTH, "OPINION");
            query.whereNotEqualTo(Article.KEY_TRUTH, "UNPROVEN");
            query.whereNotEqualTo(Article.KEY_TRUTH, null);
        }
        try {
            List<Share> results = query.find();
            Log.d("User", this.getUsername() + " has shared " + results.size() + " articles");
            return results.size();
        } catch(Exception e) {
            Log.d("User", "Error: " + e.getMessage());
            return 0;
        }

    }

    public float updateBiasAverage(int newBias){
        float average = getBiasAverage();
        int count = getArticleCount();
        float newAverage = (average + newBias) / (count + 1);
        setBiasAverage(newAverage);
        return newAverage;
    }

    public float updateFactAverage(int newFact) {
        float average = getFactAverage();
        int count = queryArticleCount(true);
        float newAverage = (average + newFact) / (count + 1);
        setFactAverage(newAverage);
        return newAverage;
    }

    public int updateArticleCount() {
        int count = queryArticleCount(false);
        if (count != -1) {
            setArticleCount(count + 1);
            return count + 1;
        } else {
            setArticleCount(0);
            return 0;
        }
    }

}
