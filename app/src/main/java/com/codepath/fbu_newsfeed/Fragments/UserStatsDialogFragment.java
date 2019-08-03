package com.codepath.fbu_newsfeed.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.R;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserStatsDialogFragment extends DialogFragment {

    private static final String TAG = "UserStatsDialogFragment";

    private Unbinder unbinder;

    @BindView(R.id.pbBiasAverage) ProgressBar pbBiasAverage;
    @BindView(R.id.pbFactAverage) ProgressBar pbFactAverage;
    @BindView(R.id.tvArticleTitleCreate) TextView tvArticleNumber;
    @BindView(R.id.tvSourceFavorite) TextView tvSourceFavorite;
    @BindView(R.id.tvTagFavorite) TextView tvTagFavorite;

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
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        User user;
        try {
            user = (User) getUser(ParseUser.getCurrentUser().getObjectId());
            user.setFavoriteStats();

            tvArticleNumber.setText(Integer.toString(user.queryArticleCount(false)));
            pbBiasAverage.setProgress((int) (20 * user.getBiasAverage() - 10));
            pbFactAverage.setProgress((int) (20 *user.getFactAverage()));

            tvSourceFavorite.setText(user.getFavoriteSource());
            tvTagFavorite.setText(user.getFavoriteTag());
        } catch (ParseException e) {
            e.printStackTrace();
            user = (User) ParseUser.getCurrentUser();
        }
    }

    private ParseUser getUser(String user_id) throws ParseException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", user_id);
        return query.getFirst();
    }

}
