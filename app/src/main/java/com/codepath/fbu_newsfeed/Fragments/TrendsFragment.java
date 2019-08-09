package com.codepath.fbu_newsfeed.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.fbu_newsfeed.Adapters.TrendsAdapter;
import com.codepath.fbu_newsfeed.BookmarksActivity;
import com.codepath.fbu_newsfeed.Helpers.EndlessRecyclerViewScrollListener;
import com.codepath.fbu_newsfeed.HomeActivity;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Bookmark;
import com.codepath.fbu_newsfeed.R;
import com.codepath.fbu_newsfeed.SearchActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TrendsFragment extends Fragment {
    public static final String TAG = "TrendsFragment";

    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.rvTrends) RecyclerView rvTrends;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;


    private TrendsAdapter adapter;
    private List<Article> articles;
    private EndlessRecyclerViewScrollListener scrollListener;

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
        super.onViewCreated(view, savedInstanceState);
        ((HomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(1).setChecked(true);

        setHasOptionsMenu(true);

        searchView.clearFocus();
        rvTrends.requestFocus();

        articles = new ArrayList<>();
        adapter = new TrendsAdapter(getContext(), articles);

        rvTrends.setAdapter(adapter);
        //final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        final StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL);
        rvTrends.setLayoutManager(gridLayoutManager);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d(TAG, "FOCUSED: " + b);

                if (b) {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    getActivity().startActivity(intent);
                    searchView.clearFocus();
                    rvTrends.requestFocus();
                }

            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccentBold, R.color.colorAccentDark);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryArticles(false, page);
            }
        };

        rvTrends.addOnScrollListener(scrollListener);

        ((HomeActivity) getActivity()).toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvTrends.smoothScrollToPosition(0);
            }
        });



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim == 0) {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }

        Animation anim = android.view.animation.AnimationUtils.loadAnimation(getContext(), nextAnim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                queryArticles(true, 0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        return anim;
    }

    private void queryArticles(final boolean refresh, int offset) {
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
        articleQuery.orderByDescending(Article.KEY_CREATED_AT);
        articleQuery.orderByDescending(Article.KEY_COUNT);

        articleQuery.findInBackground(new FindCallback<Article>() {
            @Override
            public void done(List<Article> newArticles, ParseException e) {
                if (e != null) {
                    Log.e("TrendsQuery", "Error with query");
                    e.printStackTrace();
                } else {
                    articles.addAll(newArticles);
                    adapter.notifyDataSetChanged();

                }

            }
        });
    }

    private void fetchTimelineAsync() {
        adapter.clear();
        queryArticles(true, 0);
        swipeContainer.setRefreshing(false);
    }

}


