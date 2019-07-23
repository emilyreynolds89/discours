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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {
    private static final String TAG = "ShareAdapter";
    public ArrayList<Share> shares;
    public static Context context;

    public ShareAdapter(ArrayList<Share> shares) {
        this.shares = shares;
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


        holder.tvLike.setText(Integer.toString(share.getCount("LIKE")));
        holder.tvDislike.setText(Integer.toString(share.getCount("DISLIKE")));
        holder.tvHappy.setText(Integer.toString(share.getCount("HAPPY")));
        holder.tvSad.setText(Integer.toString(share.getCount("SAD")));
        holder.tvAngry.setText(Integer.toString(share.getCount("ANGRY")));



        holder.ibReactionLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reaction likeReaction = getReaction("LIKE", share, currentUser);
                if (likeReaction != null) {
                    int count = destroyReaction(likeReaction, "LIKE", share);
                    holder.tvLike.setText(Integer.toString(count));
                } else {
                    int count = createReaction("LIKE", share);
                    holder.tvLike.setText(Integer.toString(count));

                    createNotification("REACTION", share, "LIKE");
                }


            }
        });

        holder.ibReactionDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reaction dislikeReaction = getReaction("DISLIKE", share, currentUser);
                if (dislikeReaction != null) {
                    int count = destroyReaction(dislikeReaction, "DISLIKE", share);
                    holder.tvDislike.setText(Integer.toString(count));
                } else {
                    int count = createReaction("DISLIKE", share);
                    holder.tvDislike.setText(Integer.toString(count));

                    createNotification("REACTION", share, "DISLIKE");
                }


            }
        });

        holder.ibReactionHappy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reaction happyReaction = getReaction("HAPPY", share, currentUser);
                if (happyReaction != null) {
                    int count = destroyReaction(happyReaction, "HAPPY", share);
                    holder.tvHappy.setText(Integer.toString(count));
                } else {
                    int count = createReaction("HAPPY", share);
                    holder.tvHappy.setText(Integer.toString(count));

                    createNotification("REACTION", share, "HAPPY");
                }

            }
        });

        holder.ibReactionSad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reaction sadReaction = getReaction("SAD", share, currentUser);
                if (sadReaction != null) {
                    int count = destroyReaction(sadReaction, "SAD", share);
                    holder.tvSad.setText(Integer.toString(count));
                } else {
                    int count = createReaction("SAD", share);
                    holder.tvSad.setText(Integer.toString(count));

                    createNotification("REACTION", share, "SAD");
                }

            }
        });

        holder.ibReactionAngry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reaction angryReaction = getReaction("ANGRY", share, currentUser);
                if (angryReaction != null) {
                    int count = destroyReaction(angryReaction, "ANGRY", share);
                    holder.tvAngry.setText(Integer.toString(count));
                } else {
                    int count = createReaction("ANGRY", share);
                    holder.tvAngry.setText(Integer.toString(count));

                    createNotification("REACTION", share, "ANGRY");
                }


            }
        });

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
        // TODO: connect listener to information button

        String captionUsername = "@" + user.getUsername() + ": ";
        holder.tvCaption.setText(captionUsername + share.getCaption());



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
    }



    @Override
    public int getItemCount() {
        return shares.size();
    }

    public void addAll(List<Share> list) {
        shares.addAll(list);
        notifyDataSetChanged();

    }

    public void clear() {
        shares.clear();
        notifyDataSetChanged();
    }

    private void goToUser(ParseUser user) {
        ((HomeActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, ProfileFragment.newInstance(user.getObjectId())).commit();
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

    private void showInformationDialog() {
        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        InformationDialogFragment informationDialog = InformationDialogFragment.newInstance();
        informationDialog.show(fm, "fragment_information");
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImageNotif) ImageView ivProfileImage;
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

    private void createNotification(String type, Share share, String typeText) {
        Log.d(TAG, "Creating notification of type: " + type);
        Notification notification = new Notification(type, (User) ParseUser.getCurrentUser(), (User) share.getUser(), share, typeText);
        notification.saveInBackground();
    }


}
