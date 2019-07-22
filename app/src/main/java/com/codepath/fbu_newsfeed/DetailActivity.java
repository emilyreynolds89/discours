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
import com.codepath.fbu_newsfeed.Adapters.RecommendAdapter;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Comment;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.codepath.fbu_newsfeed.Models.Reaction;
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

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "DetailActivity";

    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvTimeStamp) TextView tvTimestamp;
    @BindView(R.id.viewArticle) ConstraintLayout viewArticle;
    @BindView(R.id.ivArticleImage) ImageView ivArticleImage;
    @BindView(R.id.tvArticleTitle) TextView tvArticleTitle;
    @BindView(R.id.tvArticleSummary) TextView tvArticleSummary;
    @BindView(R.id.ibReactionLike) ImageButton ibReactionLike;
    @BindView(R.id.tvLike) TextView tvLike;
    @BindView(R.id.ibReactionDislike) ImageButton ibReactionDislike;
    @BindView(R.id.tvDislike) TextView tvDislike;
    @BindView(R.id.ibReactionHappy) ImageButton ibReactionHappy;
    @BindView(R.id.tvHappy) TextView tvHappy;
    @BindView(R.id.ibReactionSad) ImageButton ibReactionSad;
    @BindView(R.id.tvSad) TextView tvSad;
    @BindView(R.id.ibReactionAngry) ImageButton ibReactionAngry;
    @BindView(R.id.tvAngry) TextView tvAngry;
    @BindView(R.id.tvFactRating) TextView tvFactRating;
    @BindView(R.id.ivBias) ImageView ivBias;
    @BindView(R.id.ibInformationTrends) ImageButton ibInformation;
    @BindView(R.id.tvCaption) TextView tvCaption;
    @BindView(R.id.rvComments) RecyclerView rvComments;
    @BindView(R.id.rvRecommend) RecyclerView rvRecommend;
    @BindView(R.id.etComment) EditText etComment;
    @BindView(R.id.btnSubmit) Button btnSubmit;
    @BindView(R.id.tvTag) TextView tvTag;
    @BindView(R.id.tvSource) TextView tvSource;
    @BindView(R.id.toolbar) Toolbar toolbar;

    Share share;
    Article article;
    ParseUser user;
    User currentUser = (User) ParseUser.getCurrentUser();

    ArrayList<Comment> comments;
    ArrayList<Article> articles;
    CommentAdapter commentAdapter;
    RecommendAdapter recommendAdapter;

    ArrayList<Reaction> reactionsLike;
    ArrayList<Reaction> reactionsDislike;
    ArrayList<Reaction> reactionsHappy;
    ArrayList<Reaction> reactionsSad;
    ArrayList<Reaction> reactionsAngry;


    protected SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        comments = new ArrayList<>();
        articles = new ArrayList<>();
        commentAdapter = new CommentAdapter(getBaseContext(), comments);
        recommendAdapter = new RecommendAdapter(getBaseContext(), articles);

        reactionsLike = new ArrayList<>();
        reactionsDislike = new ArrayList<>();
        reactionsHappy = new ArrayList<>();
        reactionsSad = new ArrayList<>();
        reactionsAngry = new ArrayList<>();

        rvComments.setAdapter(commentAdapter);
        rvRecommend.setAdapter(recommendAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        LinearLayoutManager linearLayoutManagerRecommend = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        rvComments.setLayoutManager(linearLayoutManager);
        rvRecommend.setLayoutManager(linearLayoutManagerRecommend);


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

        queryReactions("LIKE");
        queryReactions("DISLIKE");
        queryReactions("HAPPY");
        queryReactions("SAD");
        queryReactions("ANGRY");

        queryRecommended();


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
        tvTag.setText(article.getTag());
        tvSource.setText(article.getSource());
        tvFactRating.setText(article.getTruth());

        int biasValue = article.getIntBias();
        switch (biasValue) {
            case 1:  ivBias.setColorFilter(Article.liberalColor);
                break;
            case 2:  ivBias.setColorFilter(Article.slightlyLiberalColor);
                break;
            case 3:  ivBias.setColorFilter(Article.moderateColor);
                break;
            case 4:  ivBias.setColorFilter(Article.slightlyConservativeColor);
                break;
            case 5:  ivBias.setColorFilter(Article.conservativeColor);
                break;
        }

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


        ibReactionLike.setOnClickListener(this);
        ibReactionDislike.setOnClickListener(this);
        ibReactionHappy.setOnClickListener(this);
        ibReactionSad.setOnClickListener(this);
        ibReactionAngry.setOnClickListener(this);

        viewArticle.setOnClickListener(this);
        //btnSubmit.setOnClickListener(this);

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

        tvLike.setText(Integer.toString(reactionsLike.size()));
        tvDislike.setText(Integer.toString(reactionsDislike.size()));
        tvHappy.setText(Integer.toString(reactionsHappy.size()));
        tvSad.setText(Integer.toString(reactionsSad.size()));
        tvAngry.setText(Integer.toString(reactionsAngry.size()));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibReactionLike:
                queryReactions("LIKE");
                int userPositionLike = userReacted(reactionsLike, currentUser);
                if (userPositionLike != -1) {
                    Reaction userReaction = reactionsLike.get(userPositionLike);
                    reactionsLike.remove(userPositionLike);
                    userReaction.deleteInBackground();
                } else {
                    Reaction newUserReaction = new Reaction(currentUser, share, "LIKE");
                    reactionsLike.add(newUserReaction);
                    newUserReaction.saveInBackground();
                }
                tvLike.setText(Integer.toString(reactionsLike.size()));
                break;
            case R.id.ibReactionDislike:
                queryReactions("DISLIKE");
                int userPositionDislike = userReacted(reactionsDislike, currentUser);
                if (userPositionDislike != -1) {
                    Reaction userReaction = reactionsDislike.get(userPositionDislike);
                    reactionsDislike.remove(userPositionDislike);
                    userReaction.deleteInBackground();
                } else {
                    Reaction newUserReaction = new Reaction(currentUser, share, "DISLIKE");
                    reactionsDislike.add(newUserReaction);
                    newUserReaction.saveInBackground();
                }
                tvDislike.setText(Integer.toString(reactionsDislike.size()));
                break;
            case R.id.ibReactionHappy:
                queryReactions("HAPPY");
                int userPositionHappy = userReacted(reactionsHappy, currentUser);
                if (userPositionHappy != -1) {
                    Reaction userReaction = reactionsHappy.get(userPositionHappy);
                    reactionsHappy.remove(userPositionHappy);
                    userReaction.deleteInBackground();
                } else {
                    Reaction newUserReaction = new Reaction(currentUser, share, "HAPPY");
                    reactionsHappy.add(newUserReaction);
                    newUserReaction.saveInBackground();
                }
                tvHappy.setText(Integer.toString(reactionsHappy.size()));
                break;
            case R.id.ibReactionSad:
                queryReactions("SAD");
                int userPositionSad = userReacted(reactionsSad, currentUser);
                if (userPositionSad != -1) {
                    Reaction userReaction = reactionsSad.get(userPositionSad);
                    reactionsSad.remove(userPositionSad);
                    userReaction.deleteInBackground();
                } else {
                    Reaction newUserReaction = new Reaction(currentUser, share, "SAD");
                    reactionsSad.add(newUserReaction);
                    newUserReaction.saveInBackground();
                }
                tvSad.setText(Integer.toString(reactionsSad.size()));
                break;
            case R.id.ibReactionAngry:
                queryReactions("ANGRY");
                int userPositionAngry = userReacted(reactionsAngry, currentUser);
                if (userPositionAngry != -1) {
                    Reaction userReaction = reactionsAngry.get(userPositionAngry);
                    reactionsAngry.remove(userPositionAngry);
                    userReaction.deleteInBackground();
                } else {
                    Reaction newUserReaction = new Reaction(currentUser, share, "ANGRY");
                    reactionsAngry.add(newUserReaction);
                    newUserReaction.saveInBackground();
                }
                tvAngry.setText(Integer.toString(reactionsAngry.size()));
                break;
            case R.id.viewArticle:
                Intent intent = new Intent(DetailActivity.this, ArticleDetailActivity.class);
                intent.putExtra("article", (Serializable) article);
                startActivity(intent);
                break;
        }

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

    private void queryReactions(final String type) {
        ParseQuery<Reaction> reactionQuery = ParseQuery.getQuery(Reaction.class);
        reactionQuery.include(Reaction.KEY_TYPE);
        reactionQuery.include(Reaction.KEY_SHARE);
        reactionQuery.include(Reaction.KEY_USER);
        reactionQuery.whereEqualTo(Reaction.KEY_SHARE, share);
        reactionQuery.whereEqualTo(Reaction.KEY_TYPE, type);

        reactionQuery.findInBackground(new FindCallback<Reaction>() {
            @Override
            public void done(List<Reaction> newReactions, ParseException e) {
                if (e != null) {
                    Log.e("DetailActivity", "Error in reactions query");
                    e.printStackTrace();
                    return;
                }
                switch (type) {
                    case "LIKE":
                        reactionsLike.clear();
                        reactionsLike.addAll(newReactions);
                        break;
                    case "DISLIKE":
                        reactionsDislike.clear();
                        reactionsDislike.addAll(newReactions);
                        break;
                    case "HAPPY":
                        reactionsHappy.clear();
                        reactionsHappy.addAll(newReactions);
                        break;
                    case "SAD":
                        reactionsSad.clear();
                        reactionsSad.addAll(newReactions);
                        break;
                    case "ANGRY":
                        reactionsAngry.clear();
                        reactionsAngry.addAll(newReactions);
                        break;
                    default:
                        break;
                }

            }
        });
    }
    private void queryRecommended() {
        ParseQuery<Article> recommendQuery = ParseQuery.getQuery(Article.class);
        recommendQuery.include(Article.KEY_TITLE);
        recommendQuery.include(Article.KEY_IMAGE);
        recommendQuery.findInBackground(new FindCallback<Article>() {
            @Override
            public void done(List<Article> newArticles, ParseException e) {
                if (e != null) {
                    Log.e("TrendsQuery", "Error with query");
                    e.printStackTrace();
                    return;
                }
                articles.addAll(newArticles);

                for (int i = 0; i < articles.size(); i++) {
                    Article article = articles.get(i);
                    Log.d("TrendsQuery", "Article: " + article.getTitle());
                }
                recommendAdapter.notifyDataSetChanged();
            }
        });
    }

    public void fetchTimelineAsync() {
        commentAdapter.clear();
        queryComments(true);
        swipeContainer.setRefreshing(false);
    }

    public int userReacted(ArrayList<Reaction> reactions, User user) {
        for (Reaction r : reactions) {
            if (r.getUser().getObjectId().equals(user.getObjectId())) {
                return reactions.indexOf(r);
            }
        }
        return -1;
    }

}
