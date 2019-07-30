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

import com.codepath.fbu_newsfeed.Adapters.UserAdapter;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserSearchFragment extends Fragment {

    private static final String TAG = "UserSearchFragment";

    @BindView(R.id.tvError) TextView tvError;
    @BindView(R.id.searchView) SearchView searchView;
    @BindView(R.id.rvResults) RecyclerView rvResults;
    private Unbinder unbinder;

    private ArrayList<ParseUser> userResults;
    private UserAdapter userAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_search, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userResults = new ArrayList<>();
        userAdapter = new UserAdapter(new ArrayList<ParseUser>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvResults.setLayoutManager(linearLayoutManager);
        rvResults.setAdapter(userAdapter);

        fetchAllUsers();

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void fetchAllUsers() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByDescending("createdAt");
        query.setLimit(20);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    userAdapter.addAll(objects);
                } else {
                    Log.d(TAG, "Error: " + e);
                }
            }
        });
    }

    private void fetchUsers(String query) {
        ParseQuery<ParseUser> usernameQuery = ParseUser.getQuery();
        usernameQuery.whereStartsWith(User.KEY_USERNAME, query); // these String fields are not indexed
        usernameQuery.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());

        ParseQuery<ParseUser> fullNameQuery = ParseUser.getQuery();
        fullNameQuery.whereStartsWith(User.KEY_FULLNAME, query);
        fullNameQuery.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());


        List<ParseQuery<ParseUser>> queries = new ArrayList<>();
        queries.add(usernameQuery);
        queries.add(fullNameQuery);
        ParseQuery<ParseUser> mainQuery = ParseQuery.or(queries);
        mainQuery.setLimit(20);

        try {
            List<ParseUser> result = mainQuery.find();
            if (result.size() > 0) {
                tvError.setVisibility(View.GONE);
                rvResults.setVisibility(View.VISIBLE);
                userAdapter.addAll(result);
            } else {
                rvResults.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
            }

        } catch(Exception e) {
            Log.d(TAG, "Error searching users " + e.getMessage());
            Toast.makeText(getContext(), "Error searching users", Toast.LENGTH_SHORT).show();
        }
    }

}
