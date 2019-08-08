package com.codepath.fbu_newsfeed;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.codepath.fbu_newsfeed.Adapters.TrendsAdapter;
import com.codepath.fbu_newsfeed.Helpers.BiasHelper;
import com.codepath.fbu_newsfeed.Helpers.EndlessRecyclerViewScrollListener;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Source;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class SourceActivity extends AppCompatActivity {

    // TODO: MAYBE ALSO LINK TO WIKIPEDIA OR SOMETHING

    public static final String TAG = "SourceActivity";
    Source source;

    @BindView(R.id.tvSourceName) TextView tvSourceName;
    @BindView(R.id.tvSourceDescription) TextView tvSourceDescription;
    @BindView(R.id.ivSourceLogo) ImageView ivSourceLogo;
    @BindView(R.id.tvFactRating) TextView tvFactRating;
    @BindView(R.id.ivBias) ImageView ivBias;
    @BindView(R.id.rvArticles) RecyclerView rvArticles;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    public @BindView(R.id.toolbar) Toolbar toolbar;

    ArrayList<Article> articles;
    TrendsAdapter adapter;

    private EndlessRecyclerViewScrollListener scrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source);

        String sourceId = getIntent().getStringExtra("source_id");

        querySource(sourceId);

        ButterKnife.bind(this);

        articles = new ArrayList<>();
        adapter = new TrendsAdapter(this, articles);

        rvArticles.setAdapter(adapter);
        final StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL);
        rvArticles.setLayoutManager(gridLayoutManager);

        tvSourceName.setText(source.getFullName());
        tvSourceDescription.setText(source.getDescription());
        tvFactRating.setText(source.getFact());

        int biasValue = source.getBias();
        BiasHelper.setBiasImageView(ivBias, biasValue);

        if (source.getLogo() != null)
            Glide.with(this).load(source.getLogo().getUrl()).transform(new RoundedCornersTransformation(12, 0)).into(ivSourceLogo);

        querySourceArticles(0);

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
                querySourceArticles(page);
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


    private void querySource(String sourceId) {
        try {
            ParseQuery<Source> query = new ParseQuery<>(Source.class);
            source = query.get(sourceId);
        } catch (Exception e) {
            Log.d(TAG, "Error querying source", e);
        }

    }

    private void querySourceArticles(int offset) {
        final ParseQuery<Article> articleQuery = new ParseQuery<Article>(Article.class);
        articleQuery.include(Article.KEY_SOURCE);
        articleQuery.whereEqualTo(Article.KEY_SOURCE, source);
        articleQuery.setLimit(Article.LIMIT);
        articleQuery.setSkip(offset * Article.LIMIT);
        articleQuery.orderByDescending("createdAt");

        articleQuery.findInBackground(new FindCallback<Article>() {
            @Override
            public void done(List<Article> newArticles, ParseException e) {
                if (e != null) {
                    Log.e("querySourceArticles", "Error with query");
                    e.printStackTrace();
                } else {
                    adapter.addAll(newArticles);
                }

            }
        });
    }

    private void fetchTimelineAsync() {
        adapter.clear();
        querySourceArticles(0);
        swipeContainer.setRefreshing(false);
        scrollListener.resetState();
    }

}
