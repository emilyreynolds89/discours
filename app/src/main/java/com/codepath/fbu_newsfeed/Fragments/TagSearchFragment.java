package com.codepath.fbu_newsfeed.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.codepath.fbu_newsfeed.Adapters.TrendsAdapter;
import com.codepath.fbu_newsfeed.Helpers.EndlessRecyclerViewScrollListener;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.R;
import com.codepath.fbu_newsfeed.SearchActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class TagSearchFragment extends Fragment {
    private static final String TAG = "TagSearchFragment";
    @BindView(R.id.tagSelector) Spinner tagSelector;
    @BindView(R.id.rvResults) RecyclerView rvResults;
    private Unbinder unbinder;

    private ArrayList<Article> articleResults;
    private TrendsAdapter trendsAdapter;

    private ArrayList<String> tagList;
    private ArrayAdapter<String> tagAdapter;
    private String selectedTag;

    EndlessRecyclerViewScrollListener scrollListener;


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
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL);
        rvResults.setLayoutManager(gridLayoutManager);
        rvResults.setAdapter(trendsAdapter);

        tagList = new ArrayList<>();

        queryTags();

        tagAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, tagList);
        tagAdapter.setDropDownViewResource(R.layout.spinner_item);
        tagSelector.setAdapter(tagAdapter);

        tagSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                trendsAdapter.clear();
                queryArticlesByTag(tagList.get(position), 0);
                selectedTag = tagList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }

        });

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryArticlesByTag(selectedTag, page);
            }
        };

        ((SearchActivity) getActivity()).toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvResults.smoothScrollToPosition(0);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void queryArticlesByTag(String tag, int offset) {
        ParseQuery<Article> query = ParseQuery.getQuery("Article");
        query.whereEqualTo(Article.KEY_TAG, tag);
        query.setLimit(Article.LIMIT);
        query.setSkip(offset * Article.LIMIT);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Article>() {
            @Override
            public void done(List<Article> objects, ParseException e) {
                trendsAdapter.addAll(objects);
            }
        });
    }

    private void queryTags() {
        ParseQuery<Article> query = ParseQuery.getQuery("Article");
        query.findInBackground(new FindCallback<Article>() {
            @Override
            public void done(List<Article> articles, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < articles.size(); i++) {
                        String tag = articles.get(i).getTag();
                        if (!tagList.contains(tag)) {
                            tagList.add(tag);
                        }
                    }
                    Collections.sort(tagList);
                    tagAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error searching by tag", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error searching by tag", e);
                }
            }
        });
    }


}
