package com.codepath.fbu_newsfeed.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class CreateFragment extends Fragment {
    protected List<Article> articles;
    protected List<String> articleList;
    Spinner spArticleListCreate;
    ImageView ivArticlePreviewCreate;
    TextView tvArticleTitleCreate;
    TextView tvFactCheckCreate;
    ImageView  ivBiasCreate;
    ImageButton ibReportCreate;
    EditText etCaptionCreate;
    Button btnShareCreate;
    Article selectedArticle;

    ArrayAdapter<String> spinnerArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spArticleListCreate = view.findViewById(R.id.spArticleListCreate);
        ivArticlePreviewCreate  = view.findViewById(R.id.ivArticlePreviewCreate);
        tvArticleTitleCreate = view.findViewById(R.id.tvArticleTitle);
        tvFactCheckCreate = view.findViewById(R.id.tvFactCheckCreate);
        ivBiasCreate = view.findViewById(R.id.ivBias);
        ibReportCreate = view.findViewById(R.id.ibReportCreate);
        etCaptionCreate = view.findViewById(R.id.etCaptionCreate);
        btnShareCreate = view.findViewById(R.id.btShareArticleCreate);

        articles = new ArrayList<>();
        articleList = new ArrayList<>();

        queryTitle(true);


        spinnerArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, articleList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spArticleListCreate.setAdapter(spinnerArrayAdapter);

        spArticleListCreate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d("CreateFragment", "Selected item at " + String.valueOf(position));
                if (!articleList.isEmpty()) {
                    tvFactCheckCreate.setText(articles.get(position).getTruth());
                    tvArticleTitleCreate.setText(articles.get(position).getTitle());
                    ParseFile imageFile = articles.get(position).getImage();

                    int biasValue = articles.get(position).getIntBias();
                    switch (biasValue) {
                        case 1:
                            ivBiasCreate.setColorFilter(Article.liberalColor);
                            break;
                        case 2:
                            ivBiasCreate.setColorFilter(Article.slightlyLiberalColor);
                            break;
                        case 3:
                            ivBiasCreate.setColorFilter(Article.moderateColor);
                            break;
                        case 4:
                            ivBiasCreate.setColorFilter(Article.slightlyConservativeColor);
                            break;
                        case 5:
                            ivBiasCreate.setColorFilter(Article.conservativeColor);
                            break;
                    }
                    if (imageFile != null) {
                        Glide.with(getContext()).load(imageFile.getUrl()).into(ivArticlePreviewCreate);
                    }
                    selectedArticle = articles.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        btnShareCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedArticle != null) {
                    final String caption = etCaptionCreate.getText().toString();
                    shareCreate(caption, selectedArticle);
                }
            }
        });

    }
    protected void queryTitle(final boolean refresh) {
        final ParseQuery<Article> articleQuery = new ParseQuery<Article>(Article.class);
        if(refresh) articleQuery.setLimit(10);
        articleQuery.addDescendingOrder(Article.KEY_CREATED_AT);

        articleQuery.findInBackground(new FindCallback<Article>() {
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
                for (int i = 0; i < articles.size(); i++) {
                    articleList.add(i, articles.get(i).getTitle());
                    spinnerArrayAdapter.notifyDataSetChanged();
                }
            }

        });
    }
    private void shareCreate(String caption, Article article) {
        //final Share share = new Share(user, newArticle, caption);
        Share share = new Share(ParseUser.getCurrentUser(), article, caption);
        share.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("ComposeFragment", "Share article success");
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, new FeedFragment()).commit();
                } else {
                    Log.e("ComposeFragment", "Error in sharing article");
                    e.printStackTrace();
                }
            }
        });
    }
}
