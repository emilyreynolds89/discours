package com.codepath.fbu_newsfeed.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.fbu_newsfeed.ArticleDetailActivity;
import com.codepath.fbu_newsfeed.DetailActivity;
import com.codepath.fbu_newsfeed.HomeActivity;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.R;
import com.codepath.fbu_newsfeed.Fragments.ProfileFragment;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {
    private static final String TAG = "ShareAdapter";
    public static ArrayList<Share> shares;
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
    public void onBindViewHolder(@NonNull ShareAdapter.ViewHolder holder, int position) {
        final Share share = shares.get(position);
        final Article article = share.getArticle();
        final ParseUser user = share.getUser();

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

        // TODO: connect listeners to reactions

        holder.tvFactRating.setText(article.getTruth());
        // TODO: set bias image
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
                intent.putExtra("share_id", share.getObjectId());
                context.startActivity(intent);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvTimeStamp) TextView tvTimestamp;
        @BindView(R.id.viewArticle) ConstraintLayout viewArticle;
        @BindView(R.id.ivArticleImage) ImageView ivArticleImage;
        @BindView(R.id.tvArticleTitleCreate) TextView tvArticleTitle;
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
        @BindView(R.id.ivBiasCreate) ImageView ivBias;
        @BindView(R.id.ibInformationTrends) ImageButton ibInformation;
        @BindView(R.id.tvCaption) TextView tvCaption;
        @BindView(R.id.btnDiscussion) Button btnDiscussion;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }


}
