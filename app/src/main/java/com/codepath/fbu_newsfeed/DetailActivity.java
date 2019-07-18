package com.codepath.fbu_newsfeed;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.codepath.fbu_newsfeed.Adapters.CommentAdapter;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Comment;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @BindView(R.id.toolbar) Toolbar toolbar;

    Share share;
    Article article;
    ParseUser user;

    ArrayList<Comment> comments;
    CommentAdapter commentAdapter;

    protected SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(getBaseContext(), comments);

        rvComments.setAdapter(commentAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        rvComments.setLayoutManager(linearLayoutManager);

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

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

        tvCaption.setText("@" + share.getUser().getUsername() + ": " + share.getCaption());

        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUser(user);
            }
        });

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUser(user);
            }
        });


        viewArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, ArticleDetailActivity.class);
                intent.putExtra("article", (Serializable) article);
                startActivity(intent);
            }
        });

        if (isFriends() || user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            etComment.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
            rvComments.setVisibility(View.VISIBLE);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = etComment.getText().toString();
                    if (message == null) {
                        Toast.makeText(getBaseContext(), "Please enter a comment", Toast.LENGTH_LONG).show();
                    } else {
                        Comment addedComment = new Comment(message, (User) ParseUser.getCurrentUser(), share);
                        addedComment.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("DetailActivity", "Success in saving comment");
                                    fetchTimelineAsync();
                                    etComment.setText("");
                                    etComment.clearFocus();
                                    etComment.setEnabled(false);
                                    return;
                                } else {
                                    Log.e("DetailActivity", "Error in creating comment");
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        } else {
                etComment.setVisibility(View.INVISIBLE);
                btnSubmit.setVisibility(View.INVISIBLE);
                rvComments.setVisibility(View.GONE);
                btnSubmit.setOnClickListener(null);

        }

        setSupportActionBar(toolbar);
        queryComments(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        return true;
    }

    private void goToUser(ParseUser user) {
        Intent intent = new Intent(DetailActivity.this, HomeActivity.class);
        intent.putExtra("user_id", user.getObjectId());
        startActivity(intent);
    }

    private boolean isFriends() {
        ParseQuery<Friendship> query1 = ParseQuery.getQuery("Friendship");
        query1.whereEqualTo("user1", ParseUser.getCurrentUser());
        query1.whereEqualTo("user2", user);
        query1.whereEqualTo("state", Friendship.stateEnumToInt(Friendship.State.Accepted));

        ParseQuery<Friendship> query2 = ParseQuery.getQuery("Friendship");
        query2.whereEqualTo("user2", ParseUser.getCurrentUser());
        query2.whereEqualTo("user1", user);
        query2.whereEqualTo("state", Friendship.stateEnumToInt(Friendship.State.Accepted));

        List<ParseQuery<Friendship>> queries = new ArrayList<ParseQuery<Friendship>>();
        queries.add(query1);
        queries.add(query2);
        ParseQuery<Friendship> mainQuery = ParseQuery.or(queries);

        try {
            List<Friendship> result = mainQuery.find();
            return result.size() > 0;
        } catch(Exception e) {
            Log.d("User", "Error: " + e.getMessage());
            return false;
        }

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

    private void queryComments(final boolean refresh) {
        ParseQuery<Comment> commentQuery = ParseQuery.getQuery(Comment.class);
        commentQuery.include(Comment.KEY_SHARE);
        commentQuery.include(Comment.KEY_TEXT);
        commentQuery.include(Comment.KEY_USER);
        commentQuery.whereEqualTo(Comment.KEY_SHARE, share);

        commentQuery.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> newComments, ParseException e) {
                if (e != null) {
                    Log.e("DetailActivity", "Error in comment query");
                    e.printStackTrace();
                    return;
                }
                comments.addAll(newComments);
                commentAdapter.notifyDataSetChanged();
            }
        });
    }

    public void fetchTimelineAsync() {
        commentAdapter.clear();
        queryComments(true);
        swipeContainer.setRefreshing(false);
    }

}
