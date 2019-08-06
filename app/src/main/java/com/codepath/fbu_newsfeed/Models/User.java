package com.codepath.fbu_newsfeed.Models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public double biasAverage;
    public double factAverage;
    public int articleCount;
    public String favoriteTag;
    public String favoriteSource;

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

    public double getBiasAverage() {
        return getDouble(KEY_BIAS_AVERAGE);
    }

    public void setBiasAverage(double biasAverage) {
        this.biasAverage = biasAverage;
        put(KEY_BIAS_AVERAGE, biasAverage);
    }

    public double getFactAverage() {
        return getDouble(KEY_FACT_AVERAGE);
    }

    public void setFactAverage(double factAverage) {
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

    public String getFavoriteSource() {
        return favoriteSource;
    }

    public String getFavoriteTag() {
        return favoriteTag;
    }


    public int queryArticleCount(boolean queryFacts) {
        ParseQuery<Share> query = ParseQuery.getQuery("Share");
        query.whereEqualTo("user", this);
        try {
            List<Share> results = query.find();
            Log.d("User", this.getUsername() + " has shared " + results.size() + " articles");
            if (queryFacts) {
                for (Share share : results) {
                    Article article = getArticle(share.getArticle().getObjectId());
                    if (article.getTruth().equals(Fact.TruthLevel.OPINION) || article.getTruth().equals(Fact.TruthLevel.UNPROVEN)) {
                        results.remove(share);
                    }
                }
            }
            return results.size();
        } catch(Exception e) {
            Log.d("User", "Error: " + e.getMessage());
            return 0;
        }

    }

    public void updateBiasAverage(int newBias){
        double average = getBiasAverage();
        int count = getArticleCount();
        double newAverage = (count*average + newBias) / (count + 1);
        setBiasAverage(newAverage);
    }

    public void updateFactAverage(int newFact) {
        int count = queryArticleCount(true);
        double average = getFactAverage();
        double newAverage = (count*average + newFact) / (count + 1);
        setFactAverage(newAverage);
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

    private Article getArticle(String article_id) throws ParseException {
        ParseQuery<Article> query = ParseQuery.getQuery(Article.class);
        query.include("tag");
        query.include("sourceObject");
        query.whereEqualTo("objectId", article_id);
        return query.getFirst();
    }

    public void setFavoriteStats() {
        ParseQuery<Share> query = ParseQuery.getQuery(Share.class);
        query.whereEqualTo("user", this);
        try {
            List<Share> results = query.find();
            Map<String, Integer> tags = new HashMap<>();
            Map<String, Integer> sources = new HashMap<>();
            Map<Integer, Integer> biases = new HashMap<>();
            Map<Integer, Integer> facts = new HashMap<>();
            for(Share share: results) {
                Article article = getArticle(share.getArticle().getObjectId());

                String tag = article.getTag();
                updateStringMap(tags, tag);
                String source = article.getSource().getFullName();
                updateStringMap(sources, source);

                Integer bias = Bias.enumToInt(article.getBias());
                updateIntMap(biases, bias);
                Integer fact = Fact.enumToInt(article.getTruth());
                updateIntMap(facts, fact);
            }
            favoriteTag = mostCommon(tags);
            favoriteSource = mostCommon(sources);
            biasAverage = getAverage(biases);
            factAverage = getAverage(facts);

            setBiasAverage(biasAverage);
            setFactAverage(factAverage);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateStringMap(Map<String, Integer> map, String key) {
        if(map.containsKey(key)) {
            Integer count = map.get(key);
            count += 1;
            map.put(key, count);
        } else {
            map.put(key, 1);
        }
    }

    private void updateIntMap(Map<Integer, Integer> map, Integer key) {
        if(map.containsKey(key)) {
            Integer count = map.get(key);
            count += 1;
            map.put(key, count);
        } else {
            map.put(key, 1);
        }
    }

    private String mostCommon(Map<String, Integer> map) {
        String mostCommonKey = null;
        for (String key: map.keySet()) {
            if(mostCommonKey == null || map.get(key) > map.get(mostCommonKey)) {
                mostCommonKey = key;
            }
        }
        return mostCommonKey;
    }

    private double getAverage(Map<Integer, Integer> map) {
        int sum = 0;
        int total = 0;
        for (Integer key: map.keySet()) {
            int count = map.get(key);
            sum += key * count;
            total += count;
        }
        return ((double) sum) / total;
    }
}
