package com.codepath.fbu_newsfeed.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.fbu_newsfeed.Adapters.TrendsAdapter;
import com.codepath.fbu_newsfeed.EndlessRecyclerViewScrollListener;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.R;
import com.codepath.fbu_newsfeed.SearchActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TrendsFragment extends Fragment {
    @BindView(R.id.searchButton) Button searchBtn;

    @BindView(R.id.rvTrends) RecyclerView rvTrends;
    protected TrendsAdapter adapter;
    protected List<Article> articles;

    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    protected EndlessRecyclerViewScrollListener scrollListener;

    private Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trends, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvTrends = view.findViewById(R.id.rvTrends);

        articles = new ArrayList<>();
        adapter = new TrendsAdapter(getContext(), articles);

        rvTrends.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTrends.setLayoutManager(linearLayoutManager);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                getActivity().startActivity(intent);
            }
        });

        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextData(page);
            }
        };

        rvTrends.addOnScrollListener(scrollListener);
        queryArticles(true, 0);

    }

    protected void queryArticles(final boolean refresh, int offset) {
        final ParseQuery<Article> articleQuery = new ParseQuery<Article>(Article.class);
        articleQuery.include(Article.KEY_IMAGE);
        articleQuery.include(Article.KEY_SOURCE);
        articleQuery.include(Article.KEY_TITLE);
        articleQuery.include(Article.KEY_SUMMARY);
        articleQuery.include(Article.KEY_BIAS);
        articleQuery.include(Article.KEY_TRUTH);
        articleQuery.include(Article.KEY_URL);
        articleQuery.setLimit(Article.LIMIT);
        articleQuery.setSkip(offset * Article.LIMIT);
        if(refresh) articleQuery.setLimit(20);

        articleQuery.findInBackground(new FindCallback<Article>() {
            @Override
            public void done(List<Article> newArticles, ParseException e) {
                if (e != null) {
                    Log.e("TrendsQuery", "Error with query");
                    e.printStackTrace();
                    return;
                }
                articles.addAll(newArticles);
                adapter.notifyDataSetChanged();

                for (int i = 0; i < articles.size(); i++) {
                    Article article = articles.get(i);
                    Log.d("TrendsQuery", "Article: " + article.getTitle());
                }

                if (!refresh) scrollListener.resetState();
            }
        });
    }

    public void fetchTimelineAsync() {
        adapter.clear();
        queryArticles(true, 0);
        swipeContainer.setRefreshing(false);
    }

    public void loadNextData(int page) {
        queryArticles(false, page);
    }
}


