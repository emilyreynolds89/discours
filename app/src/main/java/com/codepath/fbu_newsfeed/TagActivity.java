package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.codepath.fbu_newsfeed.Adapters.TrendsAdapter;
import com.codepath.fbu_newsfeed.Models.Article;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TagActivity extends AppCompatActivity {

    @BindView(R.id.tvTagHeader) TextView tvTagHeader;
    @BindView(R.id.rvArticles) RecyclerView rvArticles;

    ArrayList<Article> articles;
    TrendsAdapter adapter;

    String tag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        tag = getIntent().getStringExtra("tag");

        ButterKnife.bind(this);

        tvTagHeader.setText("Recent articles about " + tag);

        articles = new ArrayList<>();
        adapter = new TrendsAdapter(this, articles);

        rvArticles.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvArticles.setLayoutManager(linearLayoutManager);

        queryArticles(0);

    }

    private void queryArticles(int offset) {
        final ParseQuery<Article> articleQuery = new ParseQuery<Article>(Article.class);
        articleQuery.whereEqualTo(Article.KEY_TAG, tag);
        articleQuery.setLimit(Article.LIMIT);
        articleQuery.setSkip(offset * Article.LIMIT);
        articleQuery.orderByDescending("createdAt");

        articleQuery.findInBackground(new FindCallback<Article>() {
            @Override
            public void done(List<Article> newArticles, ParseException e) {
                if (e != null) {
                    Log.e("TrendsQuery", "Error with query");
                    e.printStackTrace();
                } else {
                    adapter.addAll(newArticles);

                }

            }
        });
    }
}
