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

import com.codepath.fbu_newsfeed.Adapters.ShareAdapter;
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

    @BindView(R.id.rvShares) RecyclerView rvShares;
    ArrayList<Share> shares;
    ShareAdapter shareAdapter;
    private Unbinder unbinder;

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

        queryShares();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void queryShares() {
        ParseQuery<Share> query = ParseQuery.getQuery("Share");
        query.include("user");
        query.include("article");
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
