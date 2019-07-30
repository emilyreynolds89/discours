package com.codepath.fbu_newsfeed.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.fbu_newsfeed.Adapters.TrendsAdapter;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.R;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ArticleSearchFragment extends Fragment {

    private static final String TAG = "UserSearchFragment";

    @BindView(R.id.searchView) SearchView searchView;
    @BindView(R.id.tvError) TextView tvError;

    @BindView(R.id.rvResults) RecyclerView rvResults;
    private Unbinder unbinder;

    private ArrayList<Article> articleResults;
    private TrendsAdapter trendsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_search, container, false);
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                trendsAdapter.clear();


                fetchArticles(s);

                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);

                return true;

            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void fetchArticles(String query) {
        ParseQuery<Article> titleQuery = ParseQuery.getQuery("Article");
        titleQuery.whereFullText(Article.KEY_TITLE, query);
        titleQuery.setLimit(Article.LIMIT);
        titleQuery.orderByDescending("createdAt");

//        MongoDB can't OR text queries
//
//        ParseQuery<Article> summaryQuery = ParseQuery.getQuery("Article");
//        summaryQuery.whereFullText(Article.KEY_SUMMARY, query);
//
//        List<ParseQuery<Article>> queries = new ArrayList<>();
//        queries.add(titleQuery);
//        queries.add(summaryQuery);
//        ParseQuery<Article> mainQuery = ParseQuery.or(queries);

        try {
            List<Article> result = titleQuery.find();
            if (result.size() > 0) {
                tvError.setVisibility(View.GONE);
                rvResults.setVisibility(View.VISIBLE);
                trendsAdapter.addAll(result);
            } else {
                rvResults.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
            }
        } catch(Exception e) {
            Toast.makeText(getContext(), "Error searching articles", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error searching articles " + e.getMessage());
        }
    }

}
