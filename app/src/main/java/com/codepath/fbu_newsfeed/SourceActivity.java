package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.fbu_newsfeed.Adapters.TrendsAdapter;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Source;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SourceActivity extends AppCompatActivity {
    public static final String TAG = "SourceActivity";
    Source source;

    @BindView(R.id.tvSourceName) TextView tvSourceName;
    @BindView(R.id.tvSourceDescription) TextView tvSourceDescription;
    @BindView(R.id.ivSourceLogo) ImageView ivSourceLogo;
    @BindView(R.id.rvArticles) RecyclerView rvArticles;

    ArrayList<Article> articles;
    TrendsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);

        ButterKnife.bind(this);

        articles = new ArrayList<>();
        adapter = new TrendsAdapter(this, articles);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvArticles.setLayoutManager(linearLayoutManager);



    }
}
