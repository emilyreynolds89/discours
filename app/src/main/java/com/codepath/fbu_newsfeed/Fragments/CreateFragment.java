package com.codepath.fbu_newsfeed.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.codepath.fbu_newsfeed.Helpers.JSoupResult;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.Source;
import com.codepath.fbu_newsfeed.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class CreateFragment extends Fragment {
    private static final String TAG = "Create Fragment";
    protected List<Article> articles;
    protected List<String> articleList;
    Spinner spArticleListCreate;
    EditText etUrlCreate;
    ImageView ivArticlePreviewCreate;
    TextView tvArticleTitleCreate;
    TextView tvFactCheckCreate;
    ImageView  ivBiasCreate;
    ImageButton ibReportCreate;
    EditText etCaptionCreate;
    Button btnShareCreate;
    Article selectedArticle;
    String url;
    JSoupResult jsoupResult = new JSoupResult();
    ParseFile imageParseFile;



    ArrayAdapter<String> spinnerArrayAdapter;


    class Content extends AsyncTask<String, String, JSoupResult> {
        String title = "";
        String description = "";
        String image = "";
        String source = "";
        String urlTest = "";
        @Override
        protected JSoupResult doInBackground(String... params) {

            urlTest = params[0];
            Log.d(TAG, "urlTest: " + urlTest);
            Document document = null;
            try {
                document = Jsoup.connect(urlTest).get();
                title = document.select("meta[property=\"og:title\"]").get(0).attr("content");
                description = document.select("meta[property=\"og:description\"]").get(0).attr("content");
                image = document.select("meta[property=\"og:image\"]").get(0).attr("content");
                source = document.select("meta[property=\"og:site_name\"]").get(0).attr("content");
                jsoupResult.setTitleUrl(title);
                jsoupResult.setDescription(description);
                jsoupResult.setImageUrl(image);
                jsoupResult.setSource(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsoupResult;
        }

        @Override
        protected void onPostExecute(JSoupResult jSoupResult) {
            Log.d(TAG, "TITLE: " + jSoupResult.getTitleUrl());
            Source querySource = null;
            try {
                querySource = querySource(jSoupResult.getSource().toUpperCase());
                tvFactCheckCreate.setText(querySource.getFact());
                tvArticleTitleCreate.setText(jSoupResult.getTitleUrl());

                int biasVal = querySource.getBias();
                switch (biasVal) {
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
                if (jSoupResult.getImageUrl() != null) {
                    Glide.with(getContext()).load(jSoupResult.getImageUrl()).into(ivArticlePreviewCreate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String strFact = "MIXTURE";
            int intBias = 3;
            if (querySource != null) {
                intBias = querySource.getBias();
                strFact = querySource.getFact();
            }
            //Bitmap myBitmap = getBitmapFromURL(jsoupResult.getImageUrl());
            //ParseFile imageParse = getParseFileFromBitmap(myBitmap);

            selectedArticle = new Article(urlTest, title, jsoupResult.getImageUrl(), description, Article.biasIntToEnum(intBias), strFact, source, "POLITICS");
            selectedArticle.saveInBackground();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            url = getArguments().getString("url");
        }
        return inflater.inflate(R.layout.fragment_create, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spArticleListCreate = view.findViewById(R.id.spArticleListCreate);
        etUrlCreate = view.findViewById(R.id.etURLCreate);
        ivArticlePreviewCreate  = view.findViewById(R.id.ivArticlePreviewCreate);
        tvArticleTitleCreate = view.findViewById(R.id.tvArticleTitle);
        tvFactCheckCreate = view.findViewById(R.id.tvFactCheckCreate);
        ivBiasCreate = view.findViewById(R.id.ivBias);
        ibReportCreate = view.findViewById(R.id.ibReportCreate);
        etCaptionCreate = view.findViewById(R.id.etCaptionCreate);
        btnShareCreate = view.findViewById(R.id.btShareArticleCreate);

        if (url != null) {
            new Content().execute(url);
            etUrlCreate.setText(url);
        }
        articles = new ArrayList<>();
        articleList = new ArrayList<>();
        etUrlCreate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE ||
                        keyEvent != null &&
                                keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (keyEvent == null || !keyEvent.isShiftPressed()) {
                        String url = etUrlCreate.getText().toString();
                        new Content().execute(url);
                        return true;
                    }
                }
                return false;
            }
        });

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
                    String imageUrl = articles.get(position).getImageUrl();
                    if (imageFile != null ) {
                        Glide.with(getContext()).load(imageFile.getUrl()).into(ivArticlePreviewCreate);
                    } else if (imageUrl != null) {
                        Glide.with(getContext()).load(imageUrl).into(ivArticlePreviewCreate);
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
    private Source querySource(String source) throws ParseException {
        ParseQuery<Source> query = ParseQuery.getQuery(Source.class);
        query.whereEqualTo(Source.KEY_NAME, source);
        return query.getFirst();
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public ParseFile getParseFileFromBitmap(Bitmap bitmap) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] scaledData = bos.toByteArray();

        // Save the scaled image to Parse
        imageParseFile = new ParseFile("image_to_be_saved.jpg", scaledData);
        return imageParseFile;
    }
}
