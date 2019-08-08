package com.codepath.fbu_newsfeed.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.codepath.fbu_newsfeed.Adapters.ArticleTitleSpinnerAdapter;
import com.codepath.fbu_newsfeed.Helpers.BiasHelper;
import com.codepath.fbu_newsfeed.Helpers.JSoupResult;
import com.codepath.fbu_newsfeed.HomeActivity;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Bias;
import com.codepath.fbu_newsfeed.Models.Fact;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.Source;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class CreateFragment extends Fragment {
    public static final String TAG = "CreateFragment";

    private List<Article> articles;

    public @BindView(R.id.spArticleListCreate) Spinner spArticleListCreate;
    public @BindView(R.id.ivArticlePreviewCreate) ImageView ivArticlePreviewCreate;
    public @BindView(R.id.tvArticleTitleCreate) TextView tvArticleTitleCreate;
    public @BindView(R.id.tvFactCheckCreate) TextView tvFactCheckCreate;
    public @BindView(R.id.ivBias) ImageView  ivBiasCreate;
    public @BindView(R.id.ibInformation) ImageButton ibInformation;
    public @BindView(R.id.etCaptionCreate) EditText etCaptionCreate;
    public @BindView(R.id.btShareArticleCreate) Button btnShareCreate;
    public @BindView(R.id.etURLCreate) EditText etUrlCreate;
    public @BindView(R.id.tagSelector) Spinner tagSelector;
    public @BindView(R.id.progressBarHolder) FrameLayout progressBarHolder;

    private Unbinder unbinder;

    private ArticleTitleSpinnerAdapter spinnerArrayAdapter;
    private Article selectedArticle;
    String url;
    String selectedTag;
    ParseFile imageParseFile;

    //ContentTask contentTask;

    HashMap<String, Boolean> parsedArticles; // key: article URL, value: whether or not the article has already been parsed and added to database

    private ArrayList<String> tagList;
    private ArrayAdapter<String> tagAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        unbinder = ButterKnife.bind(this, view);

        parsedArticles = new HashMap<>();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            url = getArguments().getString("url");
            if (parsedArticles.get(url) == null || parsedArticles.get(url) == false) {
                parsedArticles.put(url, false);
            }
        }

        ((HomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(2).setChecked(true);

        tagSelector.setVisibility(View.GONE);
        spArticleListCreate.setVisibility(View.VISIBLE);
        etUrlCreate.setVisibility(View.VISIBLE);

        if (url != null) {
            urlHasBeenEntered();
        }
        articles = new ArrayList<>();
        etUrlCreate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE ||
                        keyEvent != null &&
                                keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (keyEvent == null || !keyEvent.isShiftPressed()) {
                        url = etUrlCreate.getText().toString();
                        if (!url.isEmpty()) {
                            if (parsedArticles.get(url) == null || parsedArticles.get(url) == false) {
                                parsedArticles.put(url, false);
                            }
                            urlHasBeenEntered();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        spinnerArrayAdapter = new ArticleTitleSpinnerAdapter(getContext(), articles);
        spArticleListCreate.setAdapter(spinnerArrayAdapter);

        spArticleListCreate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d("CreateFragment", "Selected item at " + position);
                if (position != 0 && !articles.isEmpty()) {
                    position = position - 1;
                    selectedArticle = articles.get(position);

                    setArticleView();

                    etUrlCreate.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                etUrlCreate.setVisibility(View.VISIBLE);
                return;
            }
        });

        btnShareCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedArticle != null) {

                    if (!etUrlCreate.getText().equals("") && tagSelector.getVisibility() == View.VISIBLE) {
                        updateArticleTag();
                    }

                    final String caption = etCaptionCreate.getText().toString();
                    shareCreate(caption, selectedArticle);

                    //updateUserStats((User) ParseUser.getCurrentUser(), selectedArticle.getObjectId());
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

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (nextAnim == 0) {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }

        Animation anim = android.view.animation.AnimationUtils.loadAnimation(getContext(), nextAnim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                queryTitle();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        return anim;
    }


    public void setArticleView() {
        this.tvFactCheckCreate.setText(Fact.enumToString(selectedArticle.getTruth()));
        this.tvArticleTitleCreate.setText(selectedArticle.getTitle());

        int biasValue = this.selectedArticle.getIntBias();
        BiasHelper.setBiasImageView(this.ivBiasCreate, biasValue);

        ParseFile imageFile = this.selectedArticle.getImage();
        String imageUrl = this.selectedArticle.getImageUrl();
        if (imageFile != null ) {
            Glide.with(getContext()).load(imageFile.getUrl()).into(this.ivArticlePreviewCreate);
        } else if (imageUrl != null) {
            Glide.with(getContext()).load(imageUrl).into(this.ivArticlePreviewCreate);
        }
        if (tagSelector.getVisibility() == View.VISIBLE) {
            int spinnerPosition = tagAdapter.getPosition(this.selectedArticle.getTag());
            tagSelector.setSelection(spinnerPosition);
        }

    }


    private void urlHasBeenEntered() {
        etUrlCreate.setText(url);

        Article existingArticle = getArticleByUrl(url);
        if (existingArticle == null) {
            if (!parsedArticles.get(url)) {
                new ContentTask(CreateFragment.this).execute(url);
                parsedArticles.put(url, true);
            }
        } else {
            Log.d(TAG, "This article already exists (found with URL)");
            selectedArticle = existingArticle;
            parsedArticles.put(url, true);
            setArticleView();
        }
        spArticleListCreate.setVisibility(View.INVISIBLE);
    }



    private void setUpTagSelector() {
        tagSelector.setVisibility(View.VISIBLE);
        tagList = new ArrayList<>();

        queryTags();

        tagAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, tagList);
        tagAdapter.setDropDownViewResource(R.layout.spinner_item);
        tagSelector.setAdapter(tagAdapter);

        tagSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedTag = tagList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }

        });
    }

    private void showInformationDialog() {
        FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
        InformationDialogFragment informationDialog = InformationDialogFragment.newInstance();
        informationDialog.show(fm, "fragment_information");
    }

    private void queryTitle() {
        final ParseQuery<Article> articleQuery = new ParseQuery<>(Article.class);
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

                spinnerArrayAdapter.notifyDataSetChanged();
                }
        });
    }
    private void shareCreate(String caption, Article article) {
        progressBarHolder.setVisibility(View.VISIBLE);
        article.setCount(article.getCount() + 1);
        Share share = new Share(ParseUser.getCurrentUser(), article, caption);
        share.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("ComposeFragment", "Share article success");
                    progressBarHolder.setVisibility(View.INVISIBLE);
                    ((HomeActivity) getActivity()).bottomNavigationView.setSelectedItemId(R.id.action_home);
                    getActivity().getSupportFragmentManager().beginTransaction().remove(CreateFragment.this).commit();
                } else {
                    progressBarHolder.setVisibility(View.INVISIBLE);
                    Log.e("ComposeFragment", "Error in sharing article");
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error in sharing article", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    private Source querySource(String source) {
        try {
            ParseQuery<Source> query = ParseQuery.getQuery(Source.class);
            query.whereEqualTo(Source.KEY_NAME, source);
            return query.getFirst();
        } catch (Exception e) {
            Log.d(TAG, "Error querying source", e);
            return null;
        }
    }

    private Source matchUrlToSource(String sourceUrl) throws ParseException {
        ParseQuery<Source> query = ParseQuery.getQuery(Source.class);
        List<Source> sources = query.find();
        for (Source s : sources) {
            if (s.getUrlMatch() != null) {
                String urlMatch = s.getUrlMatch();
                Pattern p = Pattern.compile("^.+[\\/\\.]" + urlMatch + "\\..+$");
                Matcher m = p.matcher(sourceUrl);
                if (m.matches()) {
                    return s;
                }
            }
        }
        return null;
    }

    private void updateUserStats(User user, String articleId) {
        Article article = null;
        try {
            article = getArticle(articleId);
            user.updateArticleCount();
            user.updateBiasAverage(article.getIntBias());
            user.updateFactAverage(Fact.enumToInt(article.getTruth()));
            user.saveInBackground();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Article getArticle(String article_id) throws ParseException {
        ParseQuery<Article> query = ParseQuery.getQuery(Article.class);
        query.whereEqualTo("objectId", article_id);
        return query.getFirst();
    }

    private void updateArticleTag() {
        ParseQuery<Article> query = ParseQuery.getQuery("Article");
        query.getInBackground(selectedArticle.getObjectId(), new GetCallback<Article>() {
            @Override
            public void done(Article object, ParseException e) {
                if (e == null) {
                    object.setTag(selectedTag);
                    if (selectedTag.equals(Fact.enumToString(Fact.TruthLevel.OPINION))) {
                        object.setTruth(Fact.TruthLevel.OPINION);
                    }
                    object.saveInBackground();
                }
            }
        });

    }

    private void queryTags() {
        ParseQuery<Article> query = ParseQuery.getQuery("Article");
        query.findInBackground(new FindCallback<Article>() {
            @Override
            public void done(List<Article> articles, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < articles.size(); i++) {
                        String tag = articles.get(i).getTag();
                        if (!tagList.contains(tag)) {
                            tagList.add(tag);
                        }
                    }
                    tagAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error searching by tag", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error searching by tag", e);
                }
            }
        });
    }

    private Article getArticleByUrl(String url) {
        ParseQuery<Article> articleQuery = new ParseQuery<>(Article.class);
        articleQuery.whereEqualTo(Article.KEY_URL, url);
        Article result = null;
        try {
            result = articleQuery.getFirst();
        } catch (Exception e) {
            Log.d(TAG, "Error: ", e);
        }
        return result;
    }

    private Article getArticleByTitleSource(String title, Source source) {
        ParseQuery<Article> articleQuery = new ParseQuery<>(Article.class);
        articleQuery.whereEqualTo(Article.KEY_TITLE, title);
        articleQuery.whereEqualTo(Article.KEY_SOURCE, source);
        Article result = null;
        try {
            result = articleQuery.getFirst();
        } catch (Exception e) {
            Log.d(TAG, "Error: ", e);
        }
        return result;
    }

    public static class ContentTask extends AsyncTask<String, Void, JSoupResult> {
        CreateFragment fragment;

        ContentTask(CreateFragment context){
            fragment = context;
        }


        @Override
        protected void onPreExecute() {
            fragment.progressBarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSoupResult doInBackground(String... params) {
            JSoupResult jsoupResult = new JSoupResult();

            String title = "";
            String description = "";
            String image = "";
            Source source;
            String urlTest = "";
            urlTest = params[0];
            Log.d(TAG, "urlTest: " + urlTest);

            try {
                Document document = Jsoup.connect(urlTest).get();
                Elements titleSelector = document.select("meta[property=\"og:title\"]");
                if (!titleSelector.isEmpty()) {
                    title = titleSelector.get(0).attr("content");
                    jsoupResult.setTitle(title);
                }
                // TODO: what if title/description/etc. properties don't exist
                Elements descriptionSelector = document.select("meta[property=\"og:description\"]");
                if (!descriptionSelector.isEmpty()) {
                    description = descriptionSelector.get(0).attr("content");
                    jsoupResult.setDescription(description);
                }
                Elements imageSelector = document.select("meta[property=\"og:image\"]");
                if (!imageSelector.isEmpty()) {
                    image = imageSelector.get(0).attr("content");
                    jsoupResult.setImageUrl(image);
                } else {
                    Elements newImageSelector = document.select("img");
                    if (!newImageSelector.isEmpty()) {
                        image = newImageSelector.get(0).attr("src");
                        jsoupResult.setImageUrl(image);
                    }
                }
                Elements sourceSelector = document.select("meta[property=\"og:site_name\"]");
                if (!sourceSelector.isEmpty()) {
                    String sourceName = sourceSelector.get(0).attr("content");
                    jsoupResult.setSourceName(sourceName.toUpperCase());
                }

                jsoupResult.setSourceUrl(urlTest);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsoupResult;
        }

        @Override
        protected void onPostExecute(JSoupResult jSoupResult) {
            if (fragment == null || fragment.isRemoving()) return;
            Log.d(TAG, "TITLE: " + jSoupResult.getTitle());
            Source articleSource = null;
            try {
                if (jSoupResult.getSourceName() != null)
                    articleSource = fragment.querySource(jSoupResult.getSourceName());
                if (articleSource == null && jSoupResult.getSourceUrl() != null)
                    articleSource = fragment.matchUrlToSource(jSoupResult.getSourceUrl());
                else if (jSoupResult.getSourceUrl() != null)
                    articleSource = fragment.matchUrlToSource(jSoupResult.getSourceUrl());
                else
                    Log.e(TAG, "Issue getting source");

                if (articleSource != null) {
                    Log.d(TAG, "We found this source: " + articleSource.toString());
                }
                String strFact = "MIXTURE";
                int intBias = 3;
                if (articleSource != null) {
                    intBias = articleSource.getBias();
                    strFact = articleSource.getFact();
                    Article existingArticle = fragment.getArticleByTitleSource(jSoupResult.getTitle(), articleSource);
                    if (existingArticle != null) {
                        Log.d(TAG, "This article already exists (found with title and source)");
                        fragment.selectedArticle = existingArticle;
                        fragment.parsedArticles.put(jSoupResult.getSourceUrl(), true);
                        fragment.setArticleView();
                    } else {
                        fragment.selectedArticle = new Article(jSoupResult.getSourceUrl(), jSoupResult.getTitle(), jSoupResult.getImageUrl(), jSoupResult.getDescription(), Bias.intToEnum(intBias), Fact.stringToEnum(strFact), articleSource, "UNTAGGED");
                        fragment.selectedArticle.saveInBackground();
                        fragment.parsedArticles.put(jSoupResult.getSourceUrl(), true);
                        fragment.setArticleView();
                        fragment.setUpTagSelector();
                    }

                } else {
                    Toast.makeText(fragment.getContext(), "Sorry, you can't share articles from that source.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(fragment.getContext(), "Sorry, we had trouble finding the article from that URL.", Toast.LENGTH_SHORT).show();
            }
            fragment.progressBarHolder.setVisibility(View.INVISIBLE);
        }
    }

}
