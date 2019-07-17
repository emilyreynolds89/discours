package com.codepath.fbu_newsfeed;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Comment;
import com.codepath.fbu_newsfeed.Models.Share;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DetailActivity extends AppCompatActivity {
    public static final String TAG = "DetailActivity";

    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvTimeStamp) TextView tvTimestamp;
    @BindView(R.id.viewArticle) ConstraintLayout viewArticle;
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

        if (user.getParseFile("profileImage") != null) {
            Glide.with(this).load(user.getParseFile("profileImage").getUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("IMAGE_EXCEPTION", "Exception " + e.toString());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Sometimes the image is not loaded and this text is not displayed");
                            return false;                        }
                    })
                    .apply(RequestOptions.bitmapTransform(new CircleCrop())).into(ivProfileImage);
        }


        tvTimestamp.setText(share.getRelativeTime());
        ParseFile image = article.getImage();
        if (image != null ) {
            Glide.with(this).load(image.getUrl()).into(ivArticleImage);
        }
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

        viewArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, ArticleDetailActivity.class);
                intent.putExtra("article", (Serializable) article);
                startActivity(intent);
            }
        });
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
