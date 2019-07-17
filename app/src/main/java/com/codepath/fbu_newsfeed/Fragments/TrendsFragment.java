package com.codepath.fbu_newsfeed.Fragments;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.fbu_newsfeed.Adapters.TrendsAdapter;
import com.codepath.fbu_newsfeed.Models.Article;

import java.util.List;

public class TrendsFragment extends Fragment {

    protected RecyclerView rvTrends;
    protected TrendsAdapter adapter;
    protected List<Article> articles;

    protected SwipeRefreshLayout swipeContainer;




}
