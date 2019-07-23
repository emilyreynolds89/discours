package com.codepath.fbu_newsfeed.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.fbu_newsfeed.Models.UserReport;
import com.codepath.fbu_newsfeed.R;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.nio.channels.Selector;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReportUserFragment extends DialogFragment {
    public static final String TAG = "ReportUserFragment";

    public static ArrayList<String> TYPE_LIST;
    ArrayAdapter<String> typeAdapter;

    @BindView(R.id.tvReportHeader) TextView tvReportHeader;
    @BindView(R.id.spinnerType) Spinner spinnerType;
    @BindView(R.id.etComment) EditText etComment;
    @BindView(R.id.btnReport) Button btnReport;

    ParseUser reporter;
    ParseUser offender;

    private String reportType;

    private Unbinder unbinder;

    public ReportUserFragment() {}

    public static ReportUserFragment newInstance(String userId) {
        ReportUserFragment frag = new ReportUserFragment();
        Bundle args = new Bundle();
        args.putString("user_id", userId);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_user, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TYPE_LIST = new ArrayList<>();
        TYPE_LIST.add("Fake Account");
        TYPE_LIST.add("Posting Inappropriate Things");
        TYPE_LIST.add("I Want to Help");
        TYPE_LIST.add("Other");


        reporter = ParseUser.getCurrentUser();
        try {
            offender = getUser(getArguments().getString("user_id"));
        } catch(Exception e) {
            Log.d(TAG, "Trouble retrieving user", e);
            offender = reporter;
        }

        tvReportHeader.setText("Report @" + offender.getUsername());

        typeAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, TYPE_LIST);
        typeAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerType.setAdapter(typeAdapter);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                reportType = TYPE_LIST.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }

        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitReport();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private ParseUser getUser(String user_id) throws ParseException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", user_id);
        return query.getFirst();
    }

    private void submitReport() {
        if (!offender.equals(reporter)) {
            UserReport newReport = new UserReport(reporter, offender, reportType, etComment.getText().toString());
            newReport.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getContext(), "Submitted report!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Log.d(TAG, "Error submitting report", e);
                    }
                }
            });

        }
    }


}
