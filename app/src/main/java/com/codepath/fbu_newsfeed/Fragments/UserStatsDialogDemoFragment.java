package com.codepath.fbu_newsfeed.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.fbu_newsfeed.R;

public class UserStatsDialogDemoFragment extends DialogFragment {

    public UserStatsDialogDemoFragment() {}

    public static UserStatsDialogDemoFragment newInstance() {
        return new UserStatsDialogDemoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_stats_dialog_demo, container, false);
    }

}
