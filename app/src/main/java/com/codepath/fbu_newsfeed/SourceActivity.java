package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.fbu_newsfeed.Adapters.TrendsAdapter;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Source;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

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

        String sourceId = getIntent().getStringExtra("source_id");

        querySource(sourceId);

        ButterKnife.bind(this);

        articles = new ArrayList<>();
        adapter = new TrendsAdapter(this, articles);

        final StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL);
        rvArticles.setLayoutManager(gridLayoutManager);

        tvSourceName.setText(source.getFullName());
        tvSourceDescription.setText(source.getDescription());

        if (source.getLogo() != null)
            Glide.with(this).load(source.getLogo().getUrl()).into(ivSourceLogo);

        // TODO: get stuff into recycler view

    }

    private void querySource(String sourceId) {
        try {
            ParseQuery<Source> query = new ParseQuery<>(Source.class);
            source = query.get(sourceId);
        } catch (Exception e) {
            Log.d(TAG, "Error querying source", e);
        }

    }

}
