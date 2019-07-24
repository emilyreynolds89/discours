package com.codepath.fbu_newsfeed.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codepath.fbu_newsfeed.HomeActivity;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.R;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ComposeFragment extends Fragment {
    public static final String TAG = "ComposeFragment";

    Article article;

    @BindView(R.id.etURL) EditText etUrl;
    @BindView(R.id.ivArticlePreviewCreate) ImageView ivArticlePreview;
    @BindView(R.id.tvArticleTitle) TextView tvArticleTitle;
    @BindView(R.id.tvFactCheckCreate) TextView tvFactCheck;
    @BindView(R.id.ivBias) ImageView  ivBias;
    @BindView(R.id.ibReportCreate) ImageButton ibReport;
    @BindView(R.id.etCaptionCreate) EditText etCaption;
    @BindView(R.id.btShareArticleCreate) Button btnShare;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        article = (Article) getArguments().getSerializable("article");
        View view =  inflater.inflate(R.layout.fragment_compose, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((HomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(2).setChecked(true);

        final String factCheck = article.getTruth();
        final Article.Bias bias = article.getBias();
        final ParseFile imageFile = article.getImage();
        final String title = article.getTitle();
        final String summary = article.getSummary();
        final String source = article.getSource();

        tvFactCheck.setText(factCheck);
        tvArticleTitle.setText(title);
        if(imageFile != null) {
            Glide.with(getContext()).load(imageFile.getUrl()).into(ivArticlePreview);
        }

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String url = etUrl.getText().toString();
                final String caption = etCaption.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();

                shareArticle(caption, article);
            }
        });

    }

    private void shareArticle(String caption, Article article) {
        //final Share share = new Share(user, newArticle, caption);
        Share share = new Share(ParseUser.getCurrentUser(), article, caption);
        share.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("ComposeFragment", "Share article success");
                    ((HomeActivity) getActivity()).bottomNavigationView.setSelectedItemId(R.id.action_home);
                    getActivity().getSupportFragmentManager().beginTransaction().remove(ComposeFragment.this).commit();
                } else {
                    Log.e("ComposeFragment", "Error in sharing article");
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error in sharing article", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
