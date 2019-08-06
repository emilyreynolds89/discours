package com.codepath.fbu_newsfeed.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.fbu_newsfeed.QuizActivity;
import com.codepath.fbu_newsfeed.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InformationDialogFragment extends DialogFragment {

    @BindView(R.id.tvQuiz) TextView tvQuiz;

    private Unbinder unbinder;

    public InformationDialogFragment() {}

    public static InformationDialogFragment newInstance() {
        return new InformationDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_information_dialog, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        unbinder = ButterKnife.bind(this, view);

        tvQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), QuizActivity.class);
                startActivity(intent);
            }
        });
    }
}
