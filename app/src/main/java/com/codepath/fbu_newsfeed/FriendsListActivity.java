package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.codepath.fbu_newsfeed.Adapters.UserAdapter;
import com.codepath.fbu_newsfeed.Helpers.EndlessRecyclerViewScrollListener;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.codepath.fbu_newsfeed.Models.User;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendsListActivity extends AppCompatActivity {
    private static final String TAG = "FriendsListActivity";

    @BindView(R.id.rvFriends) RecyclerView rvFriends;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    private ParseUser user;
    private ArrayList<ParseUser> friends;
    private UserAdapter userAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ButterKnife.bind(this);

        try {
            String userId = getIntent().getStringExtra("user_id");
            user = getUser(userId);
        } catch (ParseException e){
            Log.e(TAG, "Error in retrieving username from user");
            e.printStackTrace();
        }

        friends = new ArrayList<>();
        userAdapter = new UserAdapter(friends);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFriends.setLayoutManager(linearLayoutManager);
        rvFriends.setAdapter(userAdapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorAccentBold, R.color.colorAccentDark);

        queryFriends(user, 0);

        setSupportActionBar(toolbar);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvFriends.smoothScrollToPosition(0);
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryFriends(user, page);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        return true;
    }

    private ParseUser getUser(String userId) throws ParseException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", userId);
        return query.getFirst();
    }

    private void queryFriends(final ParseUser user, int offset) {
        ParseQuery<Friendship> query1 = ParseQuery.getQuery("Friendship");
        query1.whereEqualTo(Friendship.KEY_USER1, user);
        query1.whereEqualTo(Friendship.KEY_STATE, Friendship.stateEnumToInt(Friendship.State.Accepted));


        ParseQuery<Friendship> query2 = ParseQuery.getQuery("Friendship");
        query2.whereEqualTo(Friendship.KEY_USER2, user);
        query2.whereEqualTo(Friendship.KEY_STATE, Friendship.stateEnumToInt(Friendship.State.Accepted));


        List<ParseQuery<Friendship>> queries = new ArrayList<ParseQuery<Friendship>>();
        queries.add(query1);
        queries.add(query2);
        ParseQuery<Friendship> mainQuery = ParseQuery.or(queries);
        mainQuery.include(Friendship.KEY_USER1);
        mainQuery.include(Friendship.KEY_USER2);
        mainQuery.setLimit(User.LIMIT);
        mainQuery.setSkip(User.LIMIT * offset);

        try {
            mainQuery.findInBackground(new FindCallback<Friendship>() {
                @Override
                public void done(List<Friendship> result, ParseException e) {
                    Log.d(TAG, "Found " + result.size() + " friendships");
                    for (int i = 0; i < result.size(); i++) {
                        Friendship friendship = result.get(i);
                        if (friendship.isUser1(user)) {
                            friends.add(friendship.getUser2());
                        } else {
                            friends.add(friendship.getUser1());
                        }
                        Log.d(TAG, "Found " + friends.size() + " friends");
                    }
                    userAdapter.notifyDataSetChanged();
                }
            });

        } catch(Exception e) {
            Log.d(TAG, "Error retrieving friends: " + e.getMessage());

        }
    }

    private void refresh() {
        userAdapter.clear();
        queryFriends(user, 0);;
        scrollListener.resetState();
        swipeContainer.setRefreshing(false);
    }
}
