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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.codepath.fbu_newsfeed.Helpers.JSoupResult;
import com.codepath.fbu_newsfeed.HomeActivity;
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
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



public class CreateFragment extends Fragment {
    public static final String TAG = "CreateFragment";

    private List<Article> articles;
    private List<String> articleList;

    @BindView(R.id.spArticleListCreate) Spinner spArticleListCreate;
    @BindView(R.id.ivArticlePreviewCreate) ImageView ivArticlePreviewCreate;
    @BindView(R.id.tvArticleTitle) TextView tvArticleTitleCreate;
    @BindView(R.id.tvFactCheckCreate) TextView tvFactCheckCreate;
    @BindView(R.id.ivBias) ImageView  ivBiasCreate;
    @BindView(R.id.ibInformation) ImageButton ibInformation;
    @BindView(R.id.etCaptionCreate) EditText etCaptionCreate;
    @BindView(R.id.btShareArticleCreate) Button btnShareCreate;
    @BindView(R.id.etURLCreate) EditText etUrlCreate;
    private Unbinder unbinder;

    private ArrayAdapter<String> spinnerArrayAdapter;
    private Article selectedArticle;
    String url;
    JSoupResult jsoupResult = new JSoupResult();
    ParseFile imageParseFile;


    class Content extends AsyncTask<String, String, JSoupResult> {
        String title = "";
        String description = "";
        String image = "";
        Source source;
        String urlTest = "";
        @Override
        protected JSoupResult doInBackground(String... params) {

            urlTest = params[0];
            Log.d(TAG, "urlTest: " + urlTest);
            Document document = null;
            try {
                document = Jsoup.connect(urlTest).get();
                Elements titleSelector = document.select("meta[property=\"og:title\"]");
                if (!titleSelector.isEmpty()) {
                    title = titleSelector.get(0).attr("content");
                    jsoupResult.setTitleUrl(title);
                }
                Elements descriptionSelector = document.select("meta[property=\"og:description\"]");
                if (!descriptionSelector.isEmpty()) {
                    description = descriptionSelector.get(0).attr("content");
                    jsoupResult.setDescription(description);
                }
                Elements imageSelector = document.select("meta[property=\"og:image\"]");
                if (!imageSelector.isEmpty()) {
                    image = imageSelector.get(0).attr("content");
                    jsoupResult.setImageUrl(image);
                }
                Elements sourceSelector = document.select("meta[property=\"og:site_name\"]");
                if (!sourceSelector.isEmpty()) {
                    String sourceName = sourceSelector.get(0).attr("content");
                    jsoupResult.setSourceName(sourceName.toUpperCase());
                } else {
                    jsoupResult.setSourceUrl(urlTest);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsoupResult;
        }

        @Override
        protected void onPostExecute(JSoupResult jSoupResult) {
            Log.d(TAG, "TITLE: " + jSoupResult.getTitleUrl());
            Source articleSource = null;
            try {
                if (jSoupResult.getSourceName() != null)
                    articleSource = querySource(jSoupResult.getSourceName());
                else if (jSoupResult.getSourceUrl() != null)
                    articleSource = matchUrlToSource(jSoupResult.getSourceUrl());
                else
                    Log.e(TAG, "Issue getting source");

                if (articleSource != null) {
                    tvFactCheckCreate.setText(articleSource.getFact());
                    tvArticleTitleCreate.setText(jSoupResult.getTitleUrl());

                    int biasVal = articleSource.getBias();
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String strFact = "MIXTURE";
            int intBias = 3;
            if (articleSource != null) {
                intBias = articleSource.getBias();
                strFact = articleSource.getFact();
            }
            //Bitmap myBitmap = getBitmapFromURL(jsoupResult.getImageUrl());
            //ParseFile imageParse = getParseFileFromBitmap(myBitmap);

            selectedArticle = new Article(urlTest, title, jsoupResult.getImageUrl(), description, Article.biasIntToEnum(intBias), strFact, articleSource, "POLITICS");
            selectedArticle.saveInBackground();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            url = getArguments().getString("url");
        }
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((HomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(2).setChecked(true);

        if (url != null) {
            etUrlCreate.setText(url);
            new Content().execute(url);
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

        queryTitle();

        spinnerArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, articleList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spArticleListCreate.setAdapter(spinnerArrayAdapter);

        spArticleListCreate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d("CreateFragment", "Selected item at " + position);
                if (!articleList.isEmpty()) {
                    tvFactCheckCreate.setText(articles.get(position).getTruth());
                    tvArticleTitleCreate.setText(articles.get(position).getTitle());
                    ParseFile imageFile = articles.get(position).getImage();

                    int biasValue = articles.get(position).getIntBias();
                    switch (biasValue) {
                        case 1:
                            ivBiasCreate.setBackgroundResource(R.drawable.liberal_icon);
                            break;
                        case 2:
                            ivBiasCreate.setBackgroundResource(R.drawable.slightly_liberal_icon);
                            break;
                        case 3:
                            ivBiasCreate.setBackgroundResource(R.drawable.moderate_icon);
                            break;
                        case 4:
                            ivBiasCreate.setBackgroundResource(R.drawable.slightly_conserv_icon);
                            break;
                        case 5:
                            ivBiasCreate.setBackgroundResource(R.drawable.liberal_icon);
                            break;
                        default:
                            ivBiasCreate.setBackgroundResource(R.drawable.moderate_icon);
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


        ibInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInformationDialog();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showInformationDialog() {
        FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
        InformationDialogFragment informationDialog = InformationDialogFragment.newInstance();
        informationDialog.show(fm, "fragment_information");
    }

    private void queryTitle() {
        final ParseQuery<Article> articleQuery = new ParseQuery<Article>(Article.class);
        articleQuery.setLimit(10);
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
        article.setCount(article.getCount() + 1);
        Share share = new Share(ParseUser.getCurrentUser(), article, caption);
        share.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("ComposeFragment", "Share article success");
                    ((HomeActivity) getActivity()).bottomNavigationView.setSelectedItemId(R.id.action_home);
                    getActivity().getSupportFragmentManager().beginTransaction().remove(CreateFragment.this).commit();
                } else {
                    Log.e("ComposeFragment", "Error in sharing article");
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error in sharing article", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    private Source querySource(String source) throws ParseException {
        ParseQuery<Source> query = ParseQuery.getQuery(Source.class);
        query.whereEqualTo(Source.KEY_NAME, source);
        return query.getFirst();
    }

    private Source matchUrlToSource(String sourceUrl) throws ParseException {
        ParseQuery<Source> query = ParseQuery.getQuery(Source.class);
        List<Source> sources = query.find();
        for (Source s : sources) {
            if (s.getUrlMatch() != null) {
                String urlMatch = s.getUrlMatch();
                Pattern p = Pattern.compile("\\." + urlMatch + "\\." );
                Matcher m = p.matcher(sourceUrl);
                if (m.matches()) {
                    return s;
                }
            }
        }
        return null;
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
