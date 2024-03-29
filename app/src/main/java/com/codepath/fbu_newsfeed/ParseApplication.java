package com.codepath.fbu_newsfeed;

import android.app.Application;

import com.codepath.fbu_newsfeed.Models.Annotation;
import com.codepath.fbu_newsfeed.Models.ArticleReport;
import com.codepath.fbu_newsfeed.Models.Bookmark;
import com.codepath.fbu_newsfeed.Models.Comment;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.ArticleReport;
import com.codepath.fbu_newsfeed.Models.Comment;
import com.codepath.fbu_newsfeed.Models.CommentReaction;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.codepath.fbu_newsfeed.Models.Notification;
import com.codepath.fbu_newsfeed.Models.Quiz;
import com.codepath.fbu_newsfeed.Models.Reaction;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.Source;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.Models.UserReport;
import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        ParseObject.registerSubclass(Article.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Friendship.class);
        ParseObject.registerSubclass(Reaction.class);
        ParseObject.registerSubclass(Share.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Notification.class);
        ParseObject.registerSubclass(UserReport.class);
        ParseObject.registerSubclass(Source.class);
        ParseObject.registerSubclass(ArticleReport.class);
        ParseObject.registerSubclass(Annotation.class);
        ParseObject.registerSubclass(CommentReaction.class);
        ParseObject.registerSubclass(Quiz.class);
        ParseObject.registerSubclass(Bookmark.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_application_id)) // should correspond to APP_ID env variable
                .clientKey(getString(R.string.client_key))  // set explicitly unless clientKey is explicitly configured on Parse server
                .clientBuilder(builder)
                .server(getString(R.string.server)).build());

    }
}