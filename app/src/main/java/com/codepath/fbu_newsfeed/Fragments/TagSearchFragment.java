package com.codepath.fbu_newsfeed.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.fbu_newsfeed.Adapters.TrendsAdapter;
import com.codepath.fbu_newsfeed.Adapters.UserAdapter;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

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

    ArrayList<String> tagList;
    ArrayAdapter<String> tagAdapter;


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

        tagList = new ArrayList<>();

        queryTags();

        tagAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, tagList);
        tagAdapter.setDropDownViewResource(R.layout.spinner_item);
        tagSelector.setAdapter(tagAdapter);

        tagSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                trendsAdapter.clear();
                queryArticlesByTag(tagList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void queryArticlesByTag(String tag) {
        ParseQuery<Article> query = ParseQuery.getQuery("Article");
        query.whereEqualTo(Article.KEY_TAG, tag);
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
                    tagAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error searching by tag", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error searching by tag", e);
                }
            }
        });
    }


}
