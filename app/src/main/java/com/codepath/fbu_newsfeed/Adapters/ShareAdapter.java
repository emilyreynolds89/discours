package com.codepath.fbu_newsfeed.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.fbu_newsfeed.BrowserActivity;
import com.codepath.fbu_newsfeed.DetailActivity;
import com.codepath.fbu_newsfeed.Fragments.InformationDialogFragment;
import com.codepath.fbu_newsfeed.Fragments.ProfileFragment;
import com.codepath.fbu_newsfeed.Fragments.ReportArticleFragment;
import com.codepath.fbu_newsfeed.Helpers.BiasHelper;
import com.codepath.fbu_newsfeed.Helpers.ReactionHelper;
import com.codepath.fbu_newsfeed.HomeActivity;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Fact;
import com.codepath.fbu_newsfeed.Models.Notification;
import com.codepath.fbu_newsfeed.Models.Reaction;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.Source;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.R;
import com.codepath.fbu_newsfeed.TagActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.codepath.fbu_newsfeed.Models.Reaction.KEY_SHARE;
import static com.codepath.fbu_newsfeed.Models.Reaction.KEY_USER;
import static com.codepath.fbu_newsfeed.Models.Reaction.ReactionType;
import static com.codepath.fbu_newsfeed.Models.Reaction.TYPES;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {
    private static final String TAG = "ShareAdapter";

    private List<Share> shares;
    private Context context;
    private Map<String, Map<ReactionType, Reaction>> allReactions; // key #1 is Share objectId, key #2 is reaction type

    private Animation open_anim, close_anim;

    boolean isOpen = false;

    public ShareAdapter(ArrayList<Share> newShares) {
        shares = newShares;
        allReactions = new HashMap<>();
    }

    @NonNull
    @Override
    public ShareAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View shareView = inflater.inflate(R.layout.article_item_expand_button, parent, false);
        return new ViewHolder(shareView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShareAdapter.ViewHolder holder, int position) {
        final Share share = shares.get(position);
        final Article article = share.getArticle();
        final ParseUser user = share.getUser();

        final User currentUser = (User) ParseUser.getCurrentUser();

        close_anim = AnimationUtils.loadAnimation(context, R.anim.btn_close);
        open_anim = AnimationUtils.loadAnimation(context, R.anim.btn_open);

        Map<ReactionType, Reaction> reactionMap = allReactions.get(share.getObjectId());

        holder.tvUsername.setText(user.getUsername());

        if (user.getParseFile("profileImage") != null) {
            Glide.with(context).load(user.getParseFile("profileImage").getUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(holder.ivProfileImage);
        }

        holder.tvTimestamp.setText(share.getRelativeTime());
        ParseFile image = article.getImage();
        String imageUrl = article.getImageUrl();
        if (image != null ) {
            Glide.with(context).load(image.getUrl()).into(holder.ivArticleImage);
        } else if (imageUrl != null) {
            Glide.with(context).load(imageUrl).into(holder.ivArticleImage);
        }

        holder.tvArticleTitle.setText(article.getTitle());
        holder.tvArticleSummary.setText(article.getSummary());

        article.getParseObject(Article.KEY_SOURCE).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                Source source = (Source) object;
                holder.tvSource.setText(source.getName());
            }
        });


        holder.tvTag.setText(article.getTag());

        for (int i = 0; i < TYPES.length; i++) {
            final ReactionType type = TYPES[i];
            final TextView tv = getTextViewFromReactionType(type, holder);
            final ImageButton ib = getImageButtonFromReactionType(type, holder);

            tv.setText(String.valueOf(share.getCount(Reaction.enumToString(type))));

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

        holder.tvFactRating.setText(Fact.enumToString(article.getTruth()));

        int biasValue = article.getIntBias();
        BiasHelper.setBiasImageView(holder.ivBias, biasValue);

        if (!share.getCaption().isEmpty()) {
            String captionUsername = "<b>@" + user.getUsername() + ": </b>";

            holder.tvCaption.setText(Html.fromHtml(captionUsername + share.getCaption(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        } else {
            holder.tvCaption.setVisibility(View.GONE);
        }

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ivProfileImageNotif) ImageView ivProfileImage;
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvTimeStamp) TextView tvTimestamp;
        @BindView(R.id.viewArticle) ConstraintLayout viewArticle;
        @BindView(R.id.ivArticleImage) ImageView ivArticleImage;
        @BindView(R.id.tvArticleTitleCreate) TextView tvArticleTitle;
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
        @BindView(R.id.cvArticleImage) CardView cvArticleImage;
        @BindView(R.id.ibReactionExpand) ImageButton ibReactionExpand;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            tvUsername.setOnClickListener(this);
            ivProfileImage.setOnClickListener(this);
            tvTag.setOnClickListener(this);
            cvArticleImage.setOnClickListener(this);
            tvArticleTitle.setOnClickListener(this);
            tvArticleSummary.setOnClickListener(this);
            btnDiscussion.setOnClickListener(this);
            ibInformation.setOnClickListener(this);
            ibReportArticle.setOnClickListener(this);
            tvFactRating.setOnClickListener(this);
            ivBias.setOnClickListener(this);

            ibReactionExpand.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Share share = shares.get(position);
                Article article = share.getArticle();
                ParseUser user = share.getUser();

                switch(view.getId()) {
                    case R.id.ibReactionExpand:
                        if(isOpen) {
                            setReactionVisibility(View.INVISIBLE);
                            setReactionAnim(close_anim);
                            setReactionClickable(false);

                            setClassificationVisibility(View.VISIBLE);
                            setClassificationAnim(open_anim);

                            isOpen = false;
                        } else {
                            setReactionVisibility(View.VISIBLE);
                            setReactionAnim(open_anim);
                            setReactionClickable(true);

                            setClassificationVisibility(View.INVISIBLE);
                            setClassificationAnim(close_anim);

                            isOpen = true;
                        }
                        break;
                    case R.id.tvUsername:
                    case R.id.ivProfileImageNotif:
                        goToUser(user);
                        break;
                    case R.id.tvTag:
                        Intent intent = new Intent(context, TagActivity.class);
                        intent.putExtra("tag", article.getTag());
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        break;
                    case R.id.cvArticleImage:
                    case R.id.tvArticleTitleCreate:
                    case R.id.tvArticleSummary:
                        goToArticle(article);
                        break;
                    case R.id.btnDiscussion:
                        intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("share", (Serializable) share);
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        break;
                    case R.id.ibInformation:
                        showInformationDialog();
                        break;
                    case R.id.ibReportArticle:
                        reportArticle(article);
                        break;
                    case R.id.ivBias:
                        showInformationDialog();
                        break;
                    case R.id.tvFactRating:
                        showInformationDialog();
                        break;
                }

                }

        }

        private void setReactionVisibility(int visibility) {
            ibReactionLike.setVisibility(visibility);
            ibReactionDislike.setVisibility(visibility);
            ibReactionHappy.setVisibility(visibility);
            ibReactionSad.setVisibility(visibility);
            ibReactionAngry.setVisibility(visibility);

            tvLike.setVisibility(visibility);
            tvDislike.setVisibility(visibility);
            tvHappy.setVisibility(visibility);
            tvSad.setVisibility(visibility);
            tvAngry.setVisibility(visibility);
        }

        private void setReactionAnim(Animation anim) {
            ibReactionLike.startAnimation(anim);
            ibReactionDislike.startAnimation(anim);
            ibReactionHappy.startAnimation(anim);
            ibReactionSad.startAnimation(anim);
            ibReactionAngry.startAnimation(anim);

            tvLike.startAnimation(anim);
            tvDislike.startAnimation(anim);
            tvHappy.startAnimation(anim);
            tvSad.startAnimation(anim);
            tvAngry.startAnimation(anim);
        }

        private void setReactionClickable(boolean clickable) {
            ibReactionLike.setClickable(clickable);
            ibReactionDislike.setClickable(clickable);
            ibReactionHappy.setClickable(clickable);
            ibReactionSad.setClickable(clickable);
            ibReactionAngry.setClickable(clickable);
        }

        private void setClassificationVisibility(int visibility) {
            tvFactRating.setVisibility(visibility);
            ivBias.setVisibility(visibility);
            ibInformation.setVisibility(visibility);
        }

        private void setClassificationAnim(Animation anim) {
            tvFactRating.startAnimation(anim);
            ivBias.startAnimation(anim);
            ibInformation.startAnimation(anim);
        }

    }

    private void goToArticle(Article article) {
//        Intent intent = new Intent(context, ArticleDetailActivity.class);
//        intent.putExtra("article", (Serializable) article);
//        context.startActivity(intent);
//        ((Activity) context).overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        Intent i = new Intent(context, BrowserActivity.class);
        i.putExtra("article", (Serializable) article);
        context.startActivity(i);
        ((Activity) context).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void getAllReactions() {
        ParseQuery<Reaction> reactionQuery = ParseQuery.getQuery(Reaction.class);

        reactionQuery.whereEqualTo(KEY_USER, ParseUser.getCurrentUser());
        reactionQuery.whereContainedIn(KEY_SHARE, shares);

        try {
            List<Reaction> result = reactionQuery.find();
            Log.d(TAG, "We found this many reactions: " + result.size());
            for (int i = 0; i < result.size(); i++) {
                Reaction reaction = result.get(i);
                Share share = reaction.getShare();
                if (allReactions.containsKey(share.getObjectId())) {
                    Map<ReactionType, Reaction> innerMap = allReactions.get(share.getObjectId());
                    innerMap.put(Reaction.stringToEnum(reaction.getType()), reaction);
                } else {
                    Map<ReactionType, Reaction> innerMap = new HashMap<>();
                    innerMap.put(Reaction.stringToEnum(reaction.getType()), reaction);
                    allReactions.put(share.getObjectId(), innerMap);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Error finding reactions: " + e.getMessage());
        }

    }


    private TextView getTextViewFromReactionType(ReactionType type, ShareAdapter.ViewHolder holder) {
        switch (type) {
            case LIKE:
                return holder.tvLike;
            case DISLIKE:
                return holder.tvDislike;
            case HAPPY:
                return holder.tvHappy;
            case SAD:
                return holder.tvSad;
            case ANGRY:
                return holder.tvAngry;
            default:
                return null;
        }
    }

    private ImageButton getImageButtonFromReactionType(ReactionType type, ShareAdapter.ViewHolder holder) {
        switch (type) {
            case LIKE:
                return holder.ibReactionLike;
            case DISLIKE:
                return holder.ibReactionDislike;
            case HAPPY:
                return holder.ibReactionHappy;
            case SAD:
                return holder.ibReactionSad;
            case ANGRY:
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
        //FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.right_in, R.anim.left_out);
        ReportArticleFragment articleReportDialog = ReportArticleFragment.newInstance(article.getObjectId());
        articleReportDialog.show(fragmentTransaction, "fragment_report");
    }

    private void showInformationDialog() {
        //FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.right_in, R.anim.left_out);
        InformationDialogFragment informationDialog = InformationDialogFragment.newInstance();
        informationDialog.show(fragmentTransaction, "fragment_information");
    }

    private void updateReactionText(ReactionType type, Share share, User currentUser, TextView textView, ImageButton imageButton) {
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

    private void createNotification(String type, Share share, ReactionType typeText) {
        Log.d(TAG, "Creating notification of type: " + type);
        User shareUser = (User) share.getUser();
        if (ParseUser.getCurrentUser().getObjectId().equals(shareUser.getObjectId())) { return; }
        Notification notification = new Notification(type, (User) ParseUser.getCurrentUser(), shareUser, share, Reaction.enumToString(typeText));
        notification.saveInBackground();
    }

    private void deleteNotification(String type, Share share, Reaction.ReactionType typeText) {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);

        query.whereEqualTo(Notification.KEY_TYPE, type);
        query.whereEqualTo(Notification.KEY_TYPE_TEXT, Reaction.enumToString(typeText));
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
