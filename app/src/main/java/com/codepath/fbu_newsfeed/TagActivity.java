package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.codepath.fbu_newsfeed.Adapters.TrendsAdapter;
import com.codepath.fbu_newsfeed.Helpers.EndlessRecyclerViewScrollListener;
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
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    public @BindView(R.id.toolbar) Toolbar toolbar;

    ArrayList<Article> articles;
    TrendsAdapter adapter;

    EndlessRecyclerViewScrollListener scrollListener;

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
        final StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL);
        rvArticles.setLayoutManager(gridLayoutManager);

        queryArticles(0);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorAccentBold, R.color.colorAccentDark);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryArticles(page);
            }
        };

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvArticles.smoothScrollToPosition(0);
            }
        });


        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        return true;
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

    private void fetchTimelineAsync() {
        adapter.clear();
        queryArticles(0);
        swipeContainer.setRefreshing(false);
        scrollListener.resetState();
    }
}
