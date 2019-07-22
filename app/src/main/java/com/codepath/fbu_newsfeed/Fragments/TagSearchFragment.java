package com.codepath.fbu_newsfeed.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.codepath.fbu_newsfeed.Adapters.TrendsAdapter;
import com.codepath.fbu_newsfeed.Adapters.UserAdapter;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.R;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class TagSearchFragment extends Fragment {
    private static final String TAG = "TagSearchFragment";
    @BindView(R.id.tagSelector) Spinner tagSelector;
    @BindView(R.id.rvResults) RecyclerView rvResults;
    private Unbinder unbinder;

    ArrayList<Article> articleResults;
    TrendsAdapter trendsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_search, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        articleResults = new ArrayList<>();
        trendsAdapter = new TrendsAdapter(getContext(), articleResults);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvResults.setLayoutManager(linearLayoutManager);
        rvResults.setAdapter(trendsAdapter);


        // TODO: query when tag is selected


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
