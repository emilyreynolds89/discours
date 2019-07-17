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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.fbu_newsfeed.Adapters.ShareAdapter;
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

import static com.codepath.fbu_newsfeed.DetailActivity.TAG;
import static com.parse.ParseObject.KEY_CREATED_AT;

public class ProfileFragment extends Fragment {
    ParseUser user;
    protected ShareAdapter adapter;
    private ImageView ivProfileImage;
    private TextView tvUsername;
    private TextView tvFullName;
    private TextView tvBio;
    private TextView tvArticleCount;
    private Button btnLogout;
    private RecyclerView rvProfilePosts;
    private ArrayList<Share> mShare;
    ShareAdapter shareAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        user = ParseUser.getCurrentUser();
        rvProfilePosts = view.findViewById(R.id.rvProfilePosts);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvArticleCount = view.findViewById(R.id.tvArticleCount);
        tvBio = view.findViewById(R.id.tvBio);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);

        // TODO: set textviews
        tvUsername.setText(user.getString(User.KEY_USERNAME));
        tvFullName.setText(user.getString(User.KEY_FULLNAME));
        tvBio.setText(user.getString(User.KEY_BIO));
        tvArticleCount.setText(getArticleCount(user));



        mShare = new ArrayList<>();

        // TODO: connect ShareAdapter to recyclerview
        shareAdapter = new ShareAdapter(mShare);
        rvProfilePosts.setAdapter(shareAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvProfilePosts.setLayoutManager(linearLayoutManager);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
        queryShares();
    }

    private void logOut() {
        ParseUser.logOut();
        Intent intent = new Intent(getActivity().getApplication(), LoginActivity.class);
        startActivity(intent);

    }

    private void queryShares() {
        ParseQuery<Share> query = ParseQuery.getQuery("Share");
        query.include("user");
        query.include("article");
        query.orderByDescending("createdAt");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
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

    public String getArticleCount(ParseUser user) {
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
