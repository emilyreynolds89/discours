package com.codepath.fbu_newsfeed.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.fbu_newsfeed.ArticleDetailActivity;
import com.codepath.fbu_newsfeed.BrowserActivity;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.R;
import com.parse.ParseFile;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder> {
    private Context context;
    private List<Article> articles;

    public RecommendAdapter(List<Article> articles) {
        this.articles = articles;
    }

    @NonNull
    @Override
    public RecommendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.recommend_article_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendAdapter.ViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.bind(article);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvArticleTitleRecommend) TextView tvArticleTitleRecommend;
        @BindView(R.id.ivArticleImageRecommend) ImageView ivArticleImageRecommend;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Article article = articles.get(position);

//                Intent intent = new Intent(context, ArticleDetailActivity.class);
//                intent.putExtra("article", (Serializable) article);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);

                Intent i = new Intent(context, BrowserActivity.class);
                i.putExtra("article", (Serializable) article);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                ((Activity) context).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        }
        void bind(Article article) {
            tvArticleTitleRecommend.setText(article.getTitle());
            Typeface typeface = ResourcesCompat.getFont(context, R.font.lato);
            tvArticleTitleRecommend.setTypeface(typeface);
            ParseFile image = article.getImage();
            String imageUrl = article.getImageUrl();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivArticleImageRecommend);
            } else if (imageUrl != null) {
                Glide.with(context).load(imageUrl).into(ivArticleImageRecommend);
                if (image != null && !(((Activity) context).isFinishing())) {
                    Glide.with(context.getApplicationContext()).load(image.getUrl()).into(ivArticleImageRecommend);
                }
            }
        }
    }

    public void addAll(List<Article> newArticles) {
        articles.addAll(newArticles);
        notifyDataSetChanged();
    }

    public void clear() {
        articles.clear();
        notifyDataSetChanged();
    }


}
