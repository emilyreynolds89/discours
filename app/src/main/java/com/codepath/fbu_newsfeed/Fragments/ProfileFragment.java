package com.codepath.fbu_newsfeed.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.fbu_newsfeed.Adapters.ShareAdapter;
import com.codepath.fbu_newsfeed.EndlessRecyclerViewScrollListener;
import com.codepath.fbu_newsfeed.LoginActivity;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.R;
import com.parse.FindCallback;
import com.parse.ParseUser;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";

    ParseUser user;

    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvFullName) TextView tvFullName;
    @BindView(R.id.tvBio) TextView tvBio;
    @BindView(R.id.tvArticleCount) TextView tvArticleCount;
    @BindView(R.id.btnLogout) Button btnLogout;
    @BindView(R.id.btnRequest) Button btnRequest;
    @BindView(R.id.rvProfilePosts) RecyclerView rvProfilePosts;

    private ShareAdapter shareAdapter;
    protected @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    protected EndlessRecyclerViewScrollListener scrollListener;

    private Unbinder unbinder;

    public static ProfileFragment newInstance(String user_id) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String user_id = getArguments().getString("user_id");

        Log.d(TAG, "get profile for: " + user_id);
        try {
            user = getUser(user_id);
            Log.d(TAG, "we're getting this user: " + user_id);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user, showing current user", e);
            user = ParseUser.getCurrentUser();
        }

        tvUsername.setText(user.getString(User.KEY_USERNAME));
        tvFullName.setText(user.getString(User.KEY_FULLNAME));
        tvBio.setText(user.getString(User.KEY_BIO));
        tvArticleCount.setText(getArticleCount());

        if (user.getParseFile("profileImage") != null) {
            Glide.with(getContext()).load(user.getParseFile("profileImage").getUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(ivProfileImage);
        }


        ArrayList<Share> mShare = new ArrayList<>();

        this.shareAdapter = new ShareAdapter(mShare);
        rvProfilePosts.setAdapter(shareAdapter);
        this.shareAdapter.clear();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvProfilePosts.setLayoutManager(linearLayoutManager);

        queryShares(true, 0);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
                scrollListener.resetState();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryShares(false, page);
            }
        };

        if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logOut();
                }
            });
        } else {
            btnLogout.setVisibility(View.INVISIBLE);
            btnRequest.setVisibility(View.VISIBLE);

            if (isFriends()) {
                btnRequest.setText("Friends!");
            } else if (sentRequest()) {
                btnRequest.setText("Requested");
            } else if (canAccept()) {
                btnRequest.setText("Accept request");
                btnRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        acceptRequest(user);
                    }
                });
            } else {
                    btnRequest.setText("Request friend");
                    btnRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestFriend(user);
                        }
                    });
                }
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    private void requestFriend(final ParseUser potentialFriend) {
        Friendship friendship = new Friendship(ParseUser.getCurrentUser(), potentialFriend, Friendship.State.Requested);
        friendship.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Friend request sent to " + potentialFriend.getObjectId());
                    btnRequest.setOnClickListener(null);
                    btnRequest.setText("Requested");
                } else {
                    Log.d(TAG, "Friend request failed", e);
                }
            }
        });
    }

    private void acceptRequest(final ParseUser requestingUser) {
        ParseQuery<Friendship> query = ParseQuery.getQuery("Friendship");
        query.whereEqualTo("user2",  ParseUser.getCurrentUser());
        query.whereEqualTo("user1", user);
        try {
            List<Friendship> results = query.find();
            if (results.size() > 0) {
                Friendship friendship = results.get(0);
                friendship.setState(Friendship.State.Accepted);
                friendship.save();
                btnRequest.setText("Friends!");
                btnRequest.setOnClickListener(null);
            }
        } catch (Exception e) {
        }
    }

    private void logOut() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseUser.logOut();
        }
        Intent intent = new Intent(getActivity().getApplication(), LoginActivity.class);
        startActivity(intent);
    }

    public void fetchTimelineAsync() {

        queryShares(true, 0);
        swipeContainer.setRefreshing(false);
    }
    private void queryShares(final boolean refresh, int offset) {
        Log.d(TAG, "this should only be getting shares from user: " + this.user.getObjectId());

        if (refresh) {
            shareAdapter.clear();
        }

        ParseQuery<Share> query = ParseQuery.getQuery("Share");
        query.whereEqualTo("user", this.user);
        query.include("user");
        query.include("article");
        query.setLimit(Share.LIMIT);
        query.setSkip(offset * Share.LIMIT);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Share>() {
            @Override
            public void done(List<Share> shareList, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Got " + shareList.size() + " shares");

                    shareAdapter.addAll(shareList);
                }
                if (!refresh) scrollListener.resetState();
            }
        });
    }

    // if the current user has requested this user to be their friend
    private boolean sentRequest() {
        ParseQuery<Friendship> query = ParseQuery.getQuery("Friendship");
        query.whereEqualTo("user1",  ParseUser.getCurrentUser());
        query.whereEqualTo("user2", user);
        try {
            List<Friendship> results = query.find();
            return results.size() > 0;
        } catch(Exception e) {
            Log.d("User", "Error: " + e.getMessage());
            return false;
        }
    }

    private boolean isFriends() {
        ParseQuery<Friendship> query1 = ParseQuery.getQuery("Friendship");
        query1.whereEqualTo("user1", ParseUser.getCurrentUser());
        query1.whereEqualTo("user2", user);
        query1.whereEqualTo("state", Friendship.stateEnumToInt(Friendship.State.Accepted));

        ParseQuery<Friendship> query2 = ParseQuery.getQuery("Friendship");
        query2.whereEqualTo("user2", ParseUser.getCurrentUser());
        query2.whereEqualTo("user1", user);
        query2.whereEqualTo("state", Friendship.stateEnumToInt(Friendship.State.Accepted));

        List<ParseQuery<Friendship>> queries = new ArrayList<ParseQuery<Friendship>>();
        queries.add(query1);
        queries.add(query2);
        ParseQuery<Friendship> mainQuery = ParseQuery.or(queries);

        try {
            List<Friendship> result = mainQuery.find();
            return result.size() > 0;
        } catch(Exception e) {
            Log.d("User", "Error: " + e.getMessage());
            return false;
        }

    }

    // if this user has requested the current user to be their friend
    private boolean canAccept() {
        ParseQuery<Friendship> query = ParseQuery.getQuery("Friendship");
        query.whereEqualTo("user2",  ParseUser.getCurrentUser());
        query.whereEqualTo("user1", user);
        try {
            List<Friendship> results = query.find();
            return results.size() > 0;
        } catch(Exception e) {
            Log.d("User", "Error: " + e.getMessage());
            return false;
        }
    }

    private ParseUser getUser(String user_id) throws ParseException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", user_id);
        return query.getFirst();
    }

    private String getArticleCount() {
        ParseQuery<Share> query = ParseQuery.getQuery("Share");
        query.whereEqualTo("user", user);
        try {
            List<Share> results = query.find();
            Log.d("User", user.getUsername() + " has shared " + results.size() + " articles");
            return results.size() + " Articles";
        } catch(Exception e) {
            Log.d("User", "Error: " + e.getMessage());
            return "error";
        }

    }

}
