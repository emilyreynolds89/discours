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
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.fbu_newsfeed.Adapters.ProfileAdapter;
import com.codepath.fbu_newsfeed.HomeActivity;
import com.codepath.fbu_newsfeed.LoginActivity;
import com.codepath.fbu_newsfeed.Models.Share;
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
    private ImageView ivProfileImage;
    private TextView tvUsername;
    private TextView tvFullName;
    private TextView tvBio;
    private TextView tvArticleCount;
    private Button btnLogout;
    private RecyclerView rvProfilePosts;
    private List<Share> mShare;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvProfilePosts = view.findViewById(R.id.rvProfilePosts);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvArticleCount = view.findViewById(R.id.tvArticleCount);
        tvBio = view.findViewById(R.id.tvBio);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        mShare = new ArrayList<>();
        //ProfileAdapter profileAdapter = new ProfileAdapter(mShare);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
        queryPosts();
    }
    private void logOut() {
        ParseUser.logOut();
        Intent intent = new Intent(getActivity().getApplication(), LoginActivity.class);
        startActivity(intent);

    }
    private void queryPosts() {
        ParseQuery<Share> postQuery = new ParseQuery<>(Share.class);
        postQuery.include(Share.KEY_USER);
        postQuery.setLimit(20);
        postQuery.addDescendingOrder(KEY_CREATED_AT);
        postQuery.findInBackground(new FindCallback<Share>() {
            @Override
            public void done(List<Share> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with query");
                    e.printStackTrace();
                    return;
                }
                mShare.addAll(posts);
                //adapter.notifyDataSetChanged();
            }
        });
    }

}
