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
import androidx.fragment.app.FragmentManager;
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
import com.codepath.fbu_newsfeed.Fragments.InformationDialogFragment;
import com.codepath.fbu_newsfeed.Fragments.ReportArticleFragment;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Comment;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.codepath.fbu_newsfeed.Models.Notification;
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

    @BindView(R.id.ivProfileImageNotif) ImageView ivProfileImage;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvTimeStamp) TextView tvTimestamp;
    @BindView(R.id.viewArticle) ConstraintLayout viewArticle;
    @BindView(R.id.ivArticleImage) ImageView ivArticleImage;
    @BindView(R.id.tvArticleTitle) TextView tvArticleTitle;
    @BindView(R.id.tvArticleSummary) TextView tvArticleSummary;
    @BindView(R.id.ibReportArticle) ImageButton ibReportArticle;
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
    @BindView(R.id.ibInformation) ImageButton ibInformation;
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
    User user;
    User currentUser = (User) ParseUser.getCurrentUser();

    ArrayList<Comment> comments;
    ArrayList<Article> articles;
    CommentAdapter commentAdapter;
    RecommendAdapter recommendAdapter;

    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        queryShare();

        comments = new ArrayList<>();
        articles = new ArrayList<>();
        commentAdapter = new CommentAdapter(getBaseContext(), comments);
        recommendAdapter = new RecommendAdapter(getBaseContext(), articles);

        rvComments.setAdapter(commentAdapter);
        rvRecommend.setAdapter(recommendAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        LinearLayoutManager linearLayoutManagerRecommend = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        rvComments.setLayoutManager(linearLayoutManager);
        rvRecommend.setLayoutManager(linearLayoutManagerRecommend);

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

        queryRecommended();

        String username = "username";
        try {
            username = user.fetchIfNeeded().getUsername();
        } catch (ParseException e){
            Log.e(TAG, "Error in retrieving username from user");
            e.printStackTrace();
        }
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

        if (!share.getCaption().isEmpty()) {
            tvCaption.setText("@" + share.getUser().getUsername() + ": " + share.getCaption());
        } else {
            tvCaption.setVisibility(View.GONE);
        }
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

        tvLike.setText(Integer.toString(share.getCount("LIKE")));
        tvDislike.setText(Integer.toString(share.getCount("DISLIKE")));
        tvHappy.setText(Integer.toString(share.getCount("HAPPY")));
        tvSad.setText(Integer.toString(share.getCount("SAD")));
        tvAngry.setText(Integer.toString(share.getCount("ANGRY")));


        ibReactionLike.setOnClickListener(this);
        ibReactionDislike.setOnClickListener(this);
        ibReactionHappy.setOnClickListener(this);
        ibReactionSad.setOnClickListener(this);
        ibReactionAngry.setOnClickListener(this);

        ibReportArticle.setOnClickListener(this);

        ibInformation.setOnClickListener(this);

        viewArticle.setOnClickListener(this);

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
                                    Toast.makeText(DetailActivity.this, "Error in submitting comment", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        createNotification("COMMENT", share, message);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibReportArticle:
                reportArticle();
                break;
            case R.id.ibReactionLike:
                updateReactionText("LIKE", share, currentUser, tvLike);
                break;

            case R.id.ibReactionDislike:
                updateReactionText("LIKE", share, currentUser, tvDislike);
                break;

            case R.id.ibReactionHappy:
                updateReactionText("LIKE", share, currentUser, tvHappy);
                break;

            case R.id.ibReactionSad:
                updateReactionText("LIKE", share, currentUser, tvSad);
                break;

            case R.id.ibReactionAngry:
                updateReactionText("LIKE", share, currentUser, tvAngry);
                break;

            case R.id.ibInformation:
                Log.d(TAG, "Clicked information");
                showInformationDialog();
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

    private void showInformationDialog() {
        FragmentManager fm = getSupportFragmentManager();
        InformationDialogFragment informationDialog = InformationDialogFragment.newInstance();
        informationDialog.show(fm, "fragment_information");
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
        share = (Share) getIntent().getSerializableExtra("share");
        user = (User) share.getUser();
        article = share.getArticle();
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

    private void updateReactionText(String type, Share share, User currentUser, TextView textView) {
        Reaction reaction = getReaction(type, share, currentUser);
        int count;
        if (reaction != null) {
            count = destroyReaction(reaction, type, share);
        } else {
            count = createReaction(type, share);
            createNotification("REACTION", share, type);
        }
        textView.setText(Integer.toString(count));
    }

    private void createNotification(String type, Share share, String typeText) {
        Log.d(TAG, "Creating notification of type: " + type);
        User shareUser = (User) share.getUser();
        if (ParseUser.getCurrentUser().getObjectId().equals(shareUser.getObjectId())) { return; }
        Notification notification = new Notification(type, (User) ParseUser.getCurrentUser(), shareUser, share, typeText);
        notification.saveInBackground();
    }

    private int createReaction(String type, Share share) {
        Log.d(TAG, "Creating reaction of type: " + type);
        Reaction newReaction = new Reaction(ParseUser.getCurrentUser(), share, type);
        newReaction.saveInBackground();
        int count = share.incrementCount(type);
        share.saveInBackground();
        return count;
    }

    private int destroyReaction(Reaction reaction, String type, Share share) {
        Log.d(TAG, "Destroying reaction of type: " + type);
        reaction.deleteInBackground();
        int count = share.decrementCount(type);
        share.saveInBackground();
        return count;
    }

    private Reaction getReaction(final String type, Share share, ParseUser user) {
        ParseQuery<Reaction> reactionQuery = ParseQuery.getQuery(Reaction.class);

        reactionQuery.include(Reaction.KEY_SHARE);
        reactionQuery.include(Reaction.KEY_USER);

        reactionQuery.whereEqualTo(Reaction.KEY_SHARE, share);
        reactionQuery.whereEqualTo(Reaction.KEY_USER, user);
        reactionQuery.whereEqualTo(Reaction.KEY_TYPE, type);

        try {
            List<Reaction> result = reactionQuery.find();
            if (result.size() > 0 ) {
                return result.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d(TAG, "Error finding reactions: " + e.getMessage());
            return null;
        }
    }

    private void queryRecommended() {
        final ParseQuery<Article> recommendQuery = ParseQuery.getQuery(Article.class);
        recommendQuery.include(Article.KEY_TITLE);
        recommendQuery.include(Article.KEY_IMAGE);

        recommendQuery.whereContains(Article.KEY_TAG, article.getTag());
        recommendQuery.whereNotEqualTo(Article.KEY_TITLE, article.getTitle());
        int biasValue = article.getIntBias();
        switch (biasValue) {
            case 1:
            case 2:
                recommendQuery.whereGreaterThanOrEqualTo(Article.KEY_BIAS, 2);
                recommendQuery.whereLessThanOrEqualTo(Article.KEY_BIAS, 4);
                recommendQuery.orderByDescending(Article.KEY_BIAS);
                break;
            case 3:  recommendQuery.addDescendingOrder(Article.KEY_CREATED_AT);
                break;
            case 4:
            case 5:
                recommendQuery.whereGreaterThanOrEqualTo(Article.KEY_BIAS, 2);
                recommendQuery.whereLessThanOrEqualTo(Article.KEY_BIAS, 4);
                recommendQuery.orderByAscending(Article.KEY_BIAS);
                break;
        }
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

    private void reportArticle() {
        FragmentManager fm = getSupportFragmentManager();
        ReportArticleFragment articleReportDialog = ReportArticleFragment.newInstance(article.getObjectId());
        articleReportDialog.show(fm, "fragment_report");
    }

}
