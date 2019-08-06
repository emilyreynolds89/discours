package com.codepath.fbu_newsfeed.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.fbu_newsfeed.Adapters.ShareAdapter;
import com.codepath.fbu_newsfeed.Helpers.EndlessRecyclerViewScrollListener;
import com.codepath.fbu_newsfeed.HomeActivity;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class FeedFragment extends Fragment {
    public final static String TAG = "FeedFragment";

    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.rvShares) RecyclerView rvShares;
    @BindView(R.id.tvNoContent) TextView tvNoContent;
    @BindView(R.id.filterChipGroup) ChipGroup filterChipGroup;
    @BindView(R.id.filterSubmit)
    Button filterSubmit;

    private ArrayList<ParseUser> friends;

    private ArrayList<Share> shares;
    private ShareAdapter shareAdapter;
    private Unbinder unbinder;

    private ArrayList<String> tagList;
    private HashMap<String, Chip> tagMap;
    private ArrayList<String> checkedTags;

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
        setHasOptionsMenu(true);
        ((HomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(0).setChecked(true);

        shares = new ArrayList<Share>();
        shareAdapter = new ShareAdapter(shares);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvShares.setLayoutManager(linearLayoutManager);
        rvShares.setAdapter(shareAdapter);

        tagList = new ArrayList<>();
        tagMap = new HashMap<>();
        checkedTags = new ArrayList<>();
        queryTags();

        friends = new ArrayList<>();
        getFriends();

        queryShares(0, false);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shareAdapter.clear();
                queryShares(0, false);
                for (Chip chip : tagMap.values()) {
                    chip.setChecked(true);
                }
                checkedTags.clear();
                checkedTags.addAll(tagList);
                scrollListener.resetState();
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccentBold, R.color.colorAccentDark);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryShares(page, true);
            }
        };

        rvShares.addOnScrollListener(scrollListener);

        ((HomeActivity) getActivity()).toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvShares.smoothScrollToPosition(0);
            }
        });

        filterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkedTags.clear();
                for(Map.Entry<String, Chip> entry : tagMap.entrySet()) {
                    String key = entry.getKey();
                    Chip value = entry.getValue();

                    if (value.isChecked()) {
                        checkedTags.add(key);
                    }
                }
                shareAdapter.clear();
                queryShares(0, true);
                toggleFilter();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miFilter:
                toggleFilter();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void toggleFilter() {
        if (filterChipGroup.getVisibility() == View.GONE) {
            filterChipGroup.setVisibility(View.VISIBLE);
        } else {
            filterChipGroup.setVisibility(View.GONE);
        }
    }

    private void initializeFilterGroup() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (String tag : tagList) {
            Chip chip = (Chip) inflater.inflate(R.layout.filter_chip, null, false);
            chip.setChecked(true);
            chip.setText(tag);
            filterChipGroup.addView(chip, 0);

            tagMap.put(tag, chip);
        }
    }

    private void queryShares(int offset, boolean byTag) {
        ArrayList<ParseUser> friendsQueryList = new ArrayList<>(friends);

        if (friends.size() == 0) {
            rvShares.setVisibility(View.INVISIBLE);
            tvNoContent.setVisibility(View.VISIBLE);
        } else {
            rvShares.setVisibility(View.VISIBLE);
            tvNoContent.setVisibility(View.INVISIBLE);
        }
        friendsQueryList.add(ParseUser.getCurrentUser());

        ParseQuery<Share> query = ParseQuery.getQuery("Share");
        query.include("user");
        query.include("article");
        query.whereContainedIn("user", friendsQueryList);
        if (byTag) {
            Log.d(TAG, "Filtering by tags: " + checkedTags.toString());
            ParseQuery<Article> innerQuery = ParseQuery.getQuery("Article");
            innerQuery.whereContainedIn(Article.KEY_TAG, checkedTags);
            query.whereMatchesQuery("article", innerQuery);
        }
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
                    checkedTags.addAll(tagList);
                    Collections.reverse(tagList);
                    initializeFilterGroup();
                } else {
                    Toast.makeText(getContext(), "Error searching by tag", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error searching by tag", e);
                }
            }
        });
    }

    private void getFriends() {

        ParseQuery<Friendship> query1 = ParseQuery.getQuery("Friendship");
        query1.whereEqualTo("user1", ParseUser.getCurrentUser());
        query1.whereEqualTo("state", Friendship.stateEnumToInt(Friendship.State.Accepted));

        ParseQuery<Friendship> query2 = ParseQuery.getQuery("Friendship");
        query2.whereEqualTo("user2", ParseUser.getCurrentUser());
        query2.whereEqualTo("state", Friendship.stateEnumToInt(Friendship.State.Accepted));

        List<ParseQuery<Friendship>> queries = new ArrayList<ParseQuery<Friendship>>();
        queries.add(query1);
        queries.add(query2);
        ParseQuery<Friendship> mainQuery = ParseQuery.or(queries);

        try {
            List<Friendship> result = mainQuery.find();
            Log.d(TAG, "Found " + result.size() + " friendships");
            for (int i = 0; i < result.size(); i++) {
                Friendship friendship = result.get(i);
                if (friendship.isUser1(ParseUser.getCurrentUser())) {
                    friends.add(friendship.getUser2());
                } else {
                    friends.add(friendship.getUser1());
                }
            }
        } catch(Exception e) {
            Log.d(TAG, "Error retrieving friends: " + e.getMessage());

        }
    }
}
