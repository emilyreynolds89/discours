package com.codepath.fbu_newsfeed.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.fbu_newsfeed.Models.Bias;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.QuizActivity;
import com.codepath.fbu_newsfeed.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserStatsDialogFragment extends DialogFragment {

    private static final String TAG = "UserStatsDialogFragment";

    private Unbinder unbinder;
    private ArrayList<User> friends = new ArrayList<>();

    @BindView(R.id.pbBiasAverage) ProgressBar pbBiasAverage;
    @BindView(R.id.pbFactAverage) ProgressBar pbFactAverage;
    @BindView(R.id.tvArticleTitleCreate) TextView tvArticleNumber;
    @BindView(R.id.tvSourceFavorite) TextView tvSourceFavorite;
    @BindView(R.id.tvTagFavorite) TextView tvTagFavorite;
    @BindView(R.id.tvFactAverage) TextView tvFactAverage;
    @BindView(R.id.tvSocialBubble) TextView tvSocialBubble;
    @BindView(R.id.tvClickHere) TextView tvClickHere;

    public UserStatsDialogFragment() {}

    public static UserStatsDialogFragment newInstance() {
        return new UserStatsDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_stats_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        queryFriends(ParseUser.getCurrentUser());

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        User user;
        try {
            user = (User) getUser(ParseUser.getCurrentUser().getObjectId());
            user.setFavoriteStats();

            int factAverage = (int) (25 * (user.getFactAverage() - 1));

            tvArticleNumber.setText(Integer.toString(user.queryArticleCount(false)));
            pbBiasAverage.setProgress((int) (25 * (user.getBiasAverage() - 1)));
            pbFactAverage.setProgress(factAverage);
            tvFactAverage.setText(Integer.toString(factAverage) + "%");

            tvSourceFavorite.setText(user.getFavoriteSource());
            tvTagFavorite.setText(user.getFavoriteTag());
        } catch (ParseException e) {
            e.printStackTrace();
            user = (User) ParseUser.getCurrentUser();
        }

        ProfileFragment profileFragment = (ProfileFragment)getTargetFragment();
        if (profileFragment != null) {
            profileFragment.progressBarHolder.setVisibility(View.INVISIBLE);
        }

        tvSocialBubble.setText(getSocialBubble(friends));

        tvClickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), QuizActivity.class);
                startActivity(intent);
            }
        });
    }

    private ParseUser getUser(String user_id) throws ParseException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", user_id);
        return query.getFirst();
    }

    private String getSocialBubble(ArrayList<User> friends) {
        double total = 0;
        int count = friends.size();

        for (User friend : friends) {
            try {
                User queriedFriend = (User) getUser(friend.getObjectId());
                double friendBias = queriedFriend.getBiasAverage();
                total += friendBias;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        int average = (int) Math.round(total / count);
        return Bias.intToEnum(average).toString();
    }

    private void queryFriends(final ParseUser user) {
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

        try {
            mainQuery.findInBackground(new FindCallback<Friendship>() {
                @Override
                public void done(List<Friendship> result, ParseException e) {
                    Log.d(TAG, "Found " + result.size() + " friendships");
                    for (int i = 0; i < result.size(); i++) {
                        Friendship friendship = result.get(i);
                        if (friendship.isUser1(user)) {
                            friends.add((User) friendship.getUser2());
                        } else {
                            friends.add((User) friendship.getUser1());
                        }
                        Log.d(TAG, "Found " + friends.size() + " friends");
                    }
                }
            });

        } catch(Exception e) {
            Log.d(TAG, "Error retrieving friends: " + e.getMessage());

        }
    }

}
