package com.codepath.fbu_newsfeed.fragments;

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
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.R;
import com.parse.FindCallback;
import com.parse.ParseUser;
import com.parse.ParseQuery;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.codepath.fbu_newsfeed.DetailActivity.TAG;
import static com.parse.ParseObject.KEY_CREATED_AT;

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

    private ArrayList<Share> mShare;
    ShareAdapter shareAdapter;
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
        String user_id = getArguments().getString("user_id");
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


        mShare = new ArrayList<>();

        shareAdapter = new ShareAdapter(mShare);
        rvProfilePosts.setAdapter(shareAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvProfilePosts.setLayoutManager(linearLayoutManager);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
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
                loadNextData();
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
            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestFriend(user);
                }
            });
        }

        queryShares(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void requestFriend(ParseUser potentialFriend) {
        // TODO: request friend functionality
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
        shareAdapter.clear();
        queryShares(true);
        swipeContainer.setRefreshing(false);
    }
    private void queryShares(final boolean refresh) {
        ParseQuery<Share> query = ParseQuery.getQuery("Share");
        query.include("user");
        query.include("article");
        query.orderByDescending("createdAt");
        query.whereEqualTo("user", user);
        if(refresh) query.setLimit(20);
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
    public void loadNextData() {
        queryShares(false);
    }

}
