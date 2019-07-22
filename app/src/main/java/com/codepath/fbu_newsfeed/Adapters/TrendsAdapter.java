package com.codepath.fbu_newsfeed.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.fbu_newsfeed.ArticleDetailActivity;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.R;
import com.parse.ParseFile;

import java.io.Serializable;
import java.util.List;

public class TrendsAdapter extends RecyclerView.Adapter<TrendsAdapter.ViewHolder> {

    static Context context;
    static List<Article> articles;

    public TrendsAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public TrendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trend_article_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendsAdapter.ViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.bind(article);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageButton ibInformationTrends;
        ImageView ivBiasTrends;
        ImageView ivArticleImage;
        TextView tvTitle;
        TextView tvSummary;
        TextView tvSource;
        TextView tvFactRatingTrends;
        TextView tvTagTrends;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ibInformationTrends = itemView.findViewById(R.id.ibInformationTrends);
            ivBiasTrends = itemView.findViewById(R.id.ivBiasTrends);
            ivArticleImage = itemView.findViewById(R.id.ivArticleImageTrends);
            tvTitle = itemView.findViewById(R.id.tvArticleTitleTrends);
            tvSummary = itemView.findViewById(R.id.tvArticleSummaryTrends);
            tvSource = itemView.findViewById(R.id.tvSourceTrends);
            tvFactRatingTrends = itemView.findViewById(R.id.tvFactRatingTrends);
            tvTagTrends = itemView.findViewById(R.id.tvTagTrends);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Article article = articles.get(position);

                Intent intent = new Intent(context, ArticleDetailActivity.class);
                intent.putExtra("article", (Serializable) article);
                context.startActivity(intent);
            }
        }

        public void bind(Article article) {
            tvTitle.setText(article.getTitle());
            tvSummary.setText(article.getSummary());
            tvSource.setText(article.getSource());
            tvFactRatingTrends.setText(article.getTruth());
            tvTagTrends.setText(article.getTag());

            int biasValue = article.getIntBias();
            switch (biasValue) {
                case 1:  ivBiasTrends.setColorFilter(Article.liberalColor);
                    break;
                case 2:  ivBiasTrends.setColorFilter(Article.slightlyLiberalColor);
                    break;
                case 3:  ivBiasTrends.setColorFilter(Article.moderateColor);
                    break;
                case 4:  ivBiasTrends.setColorFilter(Article.slightlyConservativeColor);
                    break;
                case 5:  ivBiasTrends.setColorFilter(Article.conservativeColor);
                    break;
            }

            ParseFile image = article.getImage();
            if (image != null ) {
                Glide.with(context).load(image.getUrl()).into(ivArticleImage);
            }
        }
    }

    public void clear() {
        articles.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Article> newArticles) {
        articles.addAll(newArticles);
        notifyDataSetChanged();
    }


}
