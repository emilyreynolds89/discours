package com.codepath.fbu_newsfeed.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.fbu_newsfeed.ArticleDetailActivity;
import com.codepath.fbu_newsfeed.DetailActivity;
import com.codepath.fbu_newsfeed.Fragments.InformationDialogFragment;
import com.codepath.fbu_newsfeed.Fragments.ProfileFragment;
import com.codepath.fbu_newsfeed.Fragments.ReportArticleFragment;
import com.codepath.fbu_newsfeed.Helpers.ReactionHelper;
import com.codepath.fbu_newsfeed.HomeActivity;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Notification;
import com.codepath.fbu_newsfeed.Models.Reaction;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.R;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {
    private static final String TAG = "ShareAdapter";

    public ArrayList<Share> shares;
    public Context context;
    Map<String, Map<String, Reaction>> allReactions; // key #1 is Share objectId, key #2 is reaction type

    public ShareAdapter(ArrayList<Share> shares) {
        this.shares = shares;
        allReactions = new HashMap<>();
    }

    @NonNull
    @Override
    public ShareAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View shareView = inflater.inflate(R.layout.article_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(shareView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ShareAdapter.ViewHolder holder, int position) {
        final Share share = shares.get(position);
        final Article article = share.getArticle();
        final ParseUser user = share.getUser();

        final User currentUser = (User) ParseUser.getCurrentUser();

        Map<String, Reaction> reactionMap = allReactions.get(share.getObjectId());

        holder.tvUsername.setText(user.getUsername());

        if (user.getParseFile("profileImage") != null) {
            Glide.with(context).load(user.getParseFile("profileImage").getUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(holder.ivProfileImage);
        }

        holder.tvTimestamp.setText(share.getRelativeTime());
        ParseFile image = article.getImage();
        if (image != null ) {
            Glide.with(context).load(image.getUrl()).into(holder.ivArticleImage);
        }
        holder.tvArticleTitle.setText(article.getTitle());
        holder.tvArticleSummary.setText(article.getSummary());
        holder.tvSource.setText(article.getSource());
        holder.tvTag.setText(article.getTag());

        for (int i = 0; i < Reaction.TYPES.length; i++) {
            final String type = Reaction.TYPES[i];
            final TextView tv = getTextViewFromReactionType(type, holder);
            final ImageButton ib = getImageButtonFromReactionType(type, holder);

            tv.setText(String.valueOf(share.getCount(type)));

            ib.setSelected(false);
            if (reactionMap != null && reactionMap.get(type) != null)
                ib.setSelected(true);

            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateReactionText(type, share, currentUser, tv, ib);
                }
            });
        }

        holder.tvFactRating.setText(article.getTruth());

        int biasValue = article.getIntBias();
        switch (biasValue) {
            case 1:  holder.ivBias.setColorFilter(Article.liberalColor);
                break;
            case 2:  holder.ivBias.setColorFilter(Article.slightlyLiberalColor);
                break;
            case 3:  holder.ivBias.setColorFilter(Article.moderateColor);
                break;
            case 4:  holder.ivBias.setColorFilter(Article.slightlyConservativeColor);
                break;
            case 5:  holder.ivBias.setColorFilter(Article.conservativeColor);
                break;
        }

        if (!share.getCaption().isEmpty()) {
            String captionUsername = "@" + user.getUsername() + ": ";
            holder.tvCaption.setText(captionUsername + share.getCaption());
        } else {
            holder.tvCaption.setVisibility(View.GONE);
        }


        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUser(user);
            }
        });

        holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUser(user);
            }
        });


        holder.viewArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ArticleDetailActivity.class);
                intent.putExtra("article", (Serializable) article);
                context.startActivity(intent);
            }
        });

        holder.btnDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("share", (Serializable) share);
                context.startActivity(intent);
            }
        });

        holder.ibInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Clicked information");
                showInformationDialog();
            }
        });

        holder.ibReportArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportArticle(article);
            }
        });
    }



    @Override
    public int getItemCount() {
        return shares.size();
    }

    public void addAll(List<Share> list) {
        shares.addAll(list);
        notifyDataSetChanged();
        getAllReactions();
    }

    public void clear() {
        shares.clear();
        notifyDataSetChanged();
        allReactions.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
        @BindView(R.id.btnDiscussion) Button btnDiscussion;
        @BindView(R.id.tvSource) TextView tvSource;
        @BindView(R.id.tvTag) TextView tvTag;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void getAllReactions() {
        ParseQuery<Reaction> reactionQuery = ParseQuery.getQuery(Reaction.class);

        reactionQuery.whereEqualTo(Reaction.KEY_USER, ParseUser.getCurrentUser());
        reactionQuery.whereContainedIn(Reaction.KEY_SHARE, shares);

        try {
            List<Reaction> result = reactionQuery.find();
            Log.d(TAG, "We found this many reactions: " + result.size());
            for (int i = 0; i < result.size(); i++) {
                Reaction reaction = result.get(i);
                Share share = reaction.getShare();
                if (allReactions.containsKey(share)) {
                    Map<String, Reaction> innerMap = allReactions.get(share.getObjectId());
                    innerMap.put(reaction.getType(), reaction);
                } else {
                    Map<String, Reaction> innerMap = new HashMap<>();
                    innerMap.put(reaction.getType(), reaction);
                    allReactions.put(share.getObjectId(), innerMap);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Error finding reactions: " + e.getMessage());
        }

    }


    private TextView getTextViewFromReactionType(String type, ShareAdapter.ViewHolder holder) {
        switch (type) {
            case "LIKE":
                return holder.tvLike;
            case "DISLIKE":
                return holder.tvDislike;
            case "HAPPY":
                return holder.tvHappy;
            case "SAD":
                return holder.tvSad;
            case "ANGRY":
                return holder.tvAngry;
            default:
                return null;
        }
    }

    private ImageButton getImageButtonFromReactionType(String type, ShareAdapter.ViewHolder holder) {
        switch (type) {
            case "LIKE":
                return holder.ibReactionLike;
            case "DISLIKE":
                return holder.ibReactionDislike;
            case "HAPPY":
                return holder.ibReactionHappy;
            case "SAD":
                return holder.ibReactionSad;
            case "ANGRY":
                return holder.ibReactionAngry;
            default:
                return null;
        }
    }


    private void goToUser(ParseUser user) {
        if (user.equals(ParseUser.getCurrentUser()))
            ((HomeActivity) context).bottomNavigationView.setSelectedItemId(R.id.action_profile);
        ((HomeActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, ProfileFragment.newInstance(user.getObjectId())).addToBackStack(ProfileFragment.TAG).commit();
    }

    private void reportArticle(Article article) {
        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        ReportArticleFragment articleReportDialog = ReportArticleFragment.newInstance(article.getObjectId());
        articleReportDialog.show(fm, "fragment_report");
    }

    private void showInformationDialog() {
        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        InformationDialogFragment informationDialog = InformationDialogFragment.newInstance();
        informationDialog.show(fm, "fragment_information");
    }

    private void updateReactionText(String type, Share share, User currentUser, TextView textView, ImageButton imageButton) {
        Reaction reaction = ReactionHelper.getReaction(type, share, currentUser);
        int count;
        if (reaction != null) {
            count = ReactionHelper.destroyReaction(reaction, type, share);
            imageButton.setSelected(false);
            deleteNotification(Notification.REACTION, share, type);
        } else {
            count = ReactionHelper.createReaction(type, share);
            imageButton.setSelected(true);
            createNotification(Notification.REACTION, share, type);
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

    private void deleteNotification(String type, Share share, String typeText) {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);

        query.whereEqualTo(Notification.KEY_TYPE, type);
        query.whereEqualTo(Notification.KEY_TYPE_TEXT, typeText);
        query.whereEqualTo(Notification.KEY_SEND_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Notification.KEY_RECEIVE_USER, share.getUser());

        Notification notification;

        try {
            List<Notification> result = query.find();
            if (result.size() > 0 ) {
                notification = result.get(0);
            } else {
                return;
            }
        } catch (Exception e) {
            Log.d(TAG, "Error finding reactions: " + e.getMessage());
            return;
        }

        notification.deleteInBackground();
    }


}
