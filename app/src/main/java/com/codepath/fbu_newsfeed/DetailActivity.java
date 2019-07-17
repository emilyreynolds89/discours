package com.codepath.fbu_newsfeed;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Comment;
import com.codepath.fbu_newsfeed.Models.Share;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    public static final String TAG = "DetailActivity";

    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvTimeStamp) TextView tvTimestamp;
    @BindView(R.id.ivArticleImage) ImageView ivArticleImage;
    @BindView(R.id.tvArticleTitle) TextView tvArticleTitle;
    @BindView(R.id.tvArticleSummary) TextView tvArticleSummary;
    @BindView(R.id.ibReactionLike) ImageButton ibReactionLike;
    @BindView(R.id.ibReactionDislike) ImageButton ibReactionDislike;
    @BindView(R.id.ibReactionHappy) ImageButton ibReactionHappy;
    @BindView(R.id.ibReactionSad) ImageButton ibReactionSad;
    @BindView(R.id.ibReactionAngry) ImageButton ibReactionAngry;
    @BindView(R.id.tvFactRating) TextView tvFactRating;
    @BindView(R.id.ivBias) ImageView ivBias;
    @BindView(R.id.ibInfomation) ImageButton ibInformation;
    @BindView(R.id.tvCaption) TextView tvCaption;
    @BindView(R.id.rvComments) RecyclerView rvComments;
    @BindView(R.id.etComment) EditText etComment;
    @BindView(R.id.btnSubmit) Button btnSubmit;

    Share share;
    Article article;
    ParseUser user;

    ArrayList<Comment> comments;
    //CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        queryShare();

        tvUsername.setText(user.getUsername());
        // TODO: set profile image
        tvTimestamp.setText(share.getRelativeTime());
        // TODO: set article image
        tvArticleTitle.setText(article.getTitle());
        tvArticleSummary.setText(article.getSummary());

        // TODO: connect listeners to reactions

        tvFactRating.setText(article.getTruth());
        // TODO: set bias image
        // TODO: connect listener to information button

        tvCaption.setText(share.getCaption());

        // set up comments recycler view
        //comments = new ArrayList<>();

        // TODO: comment composition functionality
    }

    private void queryShare() {
        String shareId = getIntent().getStringExtra("share_id");
        ParseQuery<Share> query = ParseQuery.getQuery(Share.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.include("user");
        query.include("article");
        try {
            share = query.get(shareId);
            user = share.getUser();
            article = share.getArticle();
        } catch(Exception e) {
            Log.d(TAG, "Error: " + e.getMessage());
        }
    }
}
