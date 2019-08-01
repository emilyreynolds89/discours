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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.ArticleReport;
import com.codepath.fbu_newsfeed.R;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReportArticleFragment extends DialogFragment {
    private static final String TAG = "ReportArticleFragment";

    private static ArrayList<String> TYPE_LIST;
    private ArrayAdapter<String> typeAdapter;

    @BindView(R.id.tvReportHeader)
    TextView tvReportHeader;
    @BindView(R.id.spinnerType)
    Spinner spinnerType;
    @BindView(R.id.etComment)
    EditText etComment;
    @BindView(R.id.btnReport)
    Button btnReport;

    private String reportType;
    private Article article;

    private Unbinder unbinder;

    public ReportArticleFragment() {}

    public static ReportArticleFragment newInstance(String articleId) {
        ReportArticleFragment frag = new ReportArticleFragment();
        Bundle args = new Bundle();
        args.putString("article_id", articleId);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TYPE_LIST = new ArrayList<>();
        TYPE_LIST.add("False News");
        TYPE_LIST.add("Inappropriate ContentTask");
        TYPE_LIST.add("Hate Speech");
        TYPE_LIST.add("Spam");
        TYPE_LIST.add("Other");

        try {
            article = getArticle(getArguments().getString("article_id"));
        } catch(Exception e) {
            Log.d(TAG, "Trouble retrieving article", e);
            dismiss();
        }

        tvReportHeader.setText("Report this article");

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

    private Article getArticle(String article_id) throws ParseException {
        ParseQuery<Article> query = ParseQuery.getQuery("Article");
        query.whereEqualTo("objectId", article_id);
        return query.getFirst();
    }

    private void submitReport() {
        ArticleReport newReport = new ArticleReport(ParseUser.getCurrentUser(), article, reportType, etComment.getText().toString());
        newReport.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getContext(), "Submitted report!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Log.d(TAG, "Error submitting report", e);
                        Toast.makeText(getContext(), "Error submitting report", Toast.LENGTH_SHORT).show();
                    }
                }
            });


    }



}
