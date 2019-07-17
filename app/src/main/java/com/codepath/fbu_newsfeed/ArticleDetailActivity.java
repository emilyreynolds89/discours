package com.codepath.fbu_newsfeed;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.fbu_newsfeed.Fragments.ComposeFragment;
import com.codepath.fbu_newsfeed.Models.Article;
import com.parse.ParseFile;

public class ArticleDetailActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivArticleImageDetail;
    TextView tvArticleTitleDetail;
    TextView tvArticleSummaryDetail;
    TextView tvArticleSourceDetail;
    Button btnLink;
    Button btnShare;

    Article article;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        ivArticleImageDetail = findViewById(R.id.ivArtcleImageDetail);
        tvArticleTitleDetail = findViewById(R.id.tvArticleTitleDetail);
        tvArticleSummaryDetail = findViewById(R.id.tvArticleSummaryDetail);
        tvArticleSourceDetail = findViewById(R.id.tvSourceDetail);
        btnLink = findViewById(R.id.btnLink);
        btnShare = findViewById(R.id.btnShare);

        article = (Article) getIntent().getSerializableExtra("article");

        tvArticleTitleDetail.setText(article.getTitle());
        tvArticleSummaryDetail.setText(article.getSummary());
        tvArticleSourceDetail.setText(article.getSource());

        url = article.getUrl();

        ParseFile image = article.getImage();
        if (image != null ) {
            Glide.with(getBaseContext()).load(image.getUrl()).into(ivArticleImageDetail);
        }

        btnLink.setOnClickListener(this);
        btnShare.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLink:
                break;
            case R.id.btnShare:
                Bundle bundle = new Bundle();
                bundle.putSerializable("article", article);
                ComposeFragment composeFragment = new ComposeFragment();
                composeFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, composeFragment).commit();

                break;
        }
    }
}
