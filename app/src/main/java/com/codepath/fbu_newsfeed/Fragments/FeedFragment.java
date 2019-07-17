package com.codepath.fbu_newsfeed.fragments;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.fbu_newsfeed.Adapters.ShareAdapter;
import com.codepath.fbu_newsfeed.EndlessRecyclerViewScrollListener;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class FeedFragment extends Fragment {
    private final String TAG = "FeedFragment";

    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.rvShares) RecyclerView rvShares;

    ArrayList<Share> shares;
    ShareAdapter shareAdapter;
    private Unbinder unbinder;

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shares = new ArrayList<Share>();
        shareAdapter = new ShareAdapter(shares);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvShares.setLayoutManager(linearLayoutManager);
        rvShares.setAdapter(shareAdapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shareAdapter.clear();
                queryShares(0);
                scrollListener.resetState();
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        queryShares(0);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryShares(page);
            }
        };

        rvShares.addOnScrollListener(scrollListener);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void queryShares(int offset) {
        ParseQuery<Share> query = ParseQuery.getQuery("Share");
        query.include("user");
        query.include("article");
        query.setLimit(Share.LIMIT);
        query.setSkip(offset * Share.LIMIT);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Share>() {
            @Override
            public void done(List<Share> shareList, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Got " + shareList.size() + " shares");
                    shareAdapter.addAll(shareList);
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }
}
