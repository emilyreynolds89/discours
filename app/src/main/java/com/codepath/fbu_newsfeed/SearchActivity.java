package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.codepath.fbu_newsfeed.Adapters.UserAdapter;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.codepath.fbu_newsfeed.Models.User;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    @BindView(R.id.searchView) SearchView searchView;
    @BindView(R.id.rvResults) RecyclerView rvResults;

    ArrayList<ParseUser> userResults;
    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        userResults = new ArrayList<>();
        userAdapter = new UserAdapter(new ArrayList<ParseUser>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvResults.setLayoutManager(linearLayoutManager);
        rvResults.setAdapter(userAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                userAdapter.clear();


                fetchUsers(s);

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

    private void fetchUsers(String query) {
        ParseQuery<ParseUser> usernameQuery = ParseUser.getQuery();
        usernameQuery.whereStartsWith(User.KEY_USERNAME, query);

        ParseQuery<ParseUser> fullNameQuery = ParseUser.getQuery();
        fullNameQuery.whereStartsWith(User.KEY_FULLNAME, query);

        List<ParseQuery<ParseUser>> queries = new ArrayList<>();
        queries.add(usernameQuery);
        queries.add(fullNameQuery);
        ParseQuery<ParseUser> mainQuery = ParseQuery.or(queries);

        try {
            List<ParseUser> result = mainQuery.find();
            userAdapter.addAll(result);
            // TODO: add to adapter and display results
        } catch(Exception e) {
            Log.d(TAG, "Error searching users " + e.getMessage());
        }
    }
}
