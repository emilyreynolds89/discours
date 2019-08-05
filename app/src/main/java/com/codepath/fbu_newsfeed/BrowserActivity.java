package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.fbu_newsfeed.Models.Annotation;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Friendship;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO: annotations don't go out of bounds of the article
// TODO: annotations are displayed as a little comment thing and you can expand if you click them
// TODO: shows that annotations are loading

public class BrowserActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BrowserActivity";

    public @BindView(R.id.webview) WebView webView;
    public @BindView(R.id.toolbar) Toolbar toolbar;
    public @BindView(R.id.btnShare) Button btnShare;
    @BindView(R.id.ibBack) ImageButton ibBack;
    @BindView(R.id.ibForward) ImageButton ibForward;
    @BindView(R.id.ibRefresh) ImageButton ibRefresh;

    public @BindView(R.id.etAnnotation) EditText etAnnotation;
    public @BindView(R.id.btnSubmit) Button btnSubmit;
    public @BindView(R.id.annotationConstraintLayout)
    ConstraintLayout annotationConstraintLayout;

    private String url;
    private Article article;

    private ArrayList<Annotation> annoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browser);
        ButterKnife.bind(this);

        annoList = new ArrayList<>();

        setSupportActionBar(toolbar);

        article = (Article) getIntent().getSerializableExtra("article");
        url = article.getUrl();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AnnotationInterface(this), "Android");
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (view.canGoBack()) {
                    ibBack.setColorFilter(ContextCompat.getColor(BrowserActivity.this, R.color.colorBlack));
                } else {
                    ibBack.setColorFilter(ContextCompat.getColor(BrowserActivity.this, R.color.colorModerate));
                }

                if (view.canGoForward()) {
                    ibForward.setColorFilter(ContextCompat.getColor(BrowserActivity.this, R.color.colorBlack));
                } else {
                    ibForward.setColorFilter(ContextCompat.getColor(BrowserActivity.this, R.color.colorModerate));
                }

                getAnnotations();

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectJS(view, R.raw.annotate);
            }
        });

        webView.loadUrl(url);

        btnShare.setOnClickListener(this);
        ibBack.setOnClickListener(this);
        ibForward.setOnClickListener(this);
        ibRefresh.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnShare:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("url", webView.getUrl());
                startActivity(intent);
                break;
            case R.id.ibBack:
                if (webView.canGoBack())
                    webView.goBack();
                break;
            case R.id.ibForward:
                if (webView.canGoForward())
                    webView.goForward();
                break;
            case R.id.ibRefresh:
                webView.reload();
                break;
        }
    }

    private void injectJS(WebView view, int scriptFile) {
        InputStream input;
        try {
            input = new BufferedInputStream(getResources().openRawResource(scriptFile));
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();

            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUpAnnotation(final int positionX, final int positionY) {
        if (!webView.getUrl().equals(url)) {
            Toast.makeText(this, "You may only annotate on the originally shared article", Toast.LENGTH_SHORT).show();
            return;
        }

        annotationConstraintLayout.setVisibility(View.VISIBLE);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etAnnotation.getText().toString();
                final Annotation annotation = new Annotation(ParseUser.getCurrentUser(), article, positionX, positionY, text);
                annoList.add(annotation);
                displayAnnotation(annotation);
                annotation.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(BrowserActivity.this, "Added annotation!", Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d(TAG, "Error saving annotation", e);
                        }
                        etAnnotation.setText("");
                        etAnnotation.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        annotationConstraintLayout.setVisibility(View.GONE);

                    }
                });
            }
        });

    }

    private void getAnnotations() {
        List<ParseUser> friends = getFriends();

        friends.add(ParseUser.getCurrentUser());

        ParseQuery<Annotation> annoQuery = new ParseQuery<>(Annotation.class);
        annoQuery.whereEqualTo(Annotation.KEY_ARTICLE, article);
        annoQuery.whereContainedIn("user", friends);
        annoQuery.findInBackground(new FindCallback<Annotation>() {
            @Override
            public void done(List<Annotation> objects, ParseException e) {
                Log.d(TAG, "Got " + objects.size() + " annotations");
                annoList.addAll(objects);
                for (Annotation anno : annoList) {
                    displayAnnotation(anno);
                }
            }
        });

    }

    private List<ParseUser> getFriends() {

        ParseQuery<Friendship> query1 = ParseQuery.getQuery("Friendship");
        query1.whereEqualTo("user1", ParseUser.getCurrentUser());
        query1.whereEqualTo("state", Friendship.stateEnumToInt(Friendship.State.Accepted));

        ParseQuery<Friendship> query2 = ParseQuery.getQuery("Friendship");
        query2.whereEqualTo("user2", ParseUser.getCurrentUser());
        query2.whereEqualTo("state", Friendship.stateEnumToInt(Friendship.State.Accepted));

        List<ParseQuery<Friendship>> queries = new ArrayList<ParseQuery<Friendship>>();
        queries.add(query1);
        queries.add(query2);
        ParseQuery<Friendship> mainQuery = ParseQuery.or(queries);

        try {
            List<Friendship> result = mainQuery.find();
            Log.d(TAG, "Found " + result.size() + " friendships");
            List<ParseUser> friends = new ArrayList<>();
            for (int i = 0; i < result.size(); i++) {
                Friendship friendship = result.get(i);
                if (friendship.isUser1(ParseUser.getCurrentUser())) {
                    friends.add(friendship.getUser2());
                } else {
                    friends.add(friendship.getUser1());
                }
                Log.d(TAG, "Found " + friends.size() + " friends");
            }
            return friends;
        } catch(Exception e) {
            Log.d(TAG, "Error retrieving friends: " + e.getMessage());

        }
        return new ArrayList<>();
    }

    private void displayAnnotation(Annotation anno) {
            String positionX = String.valueOf(anno.getPositionX()) + "px";
            String positionY = String.valueOf(anno.getPositionY()) + "px";
            String text = anno.getText();
            try {
                String username = anno.getUser().fetchIfNeeded().getUsername();
                webView.evaluateJavascript(
                        "    var body = document.querySelector('body');\n" +
                        "    console.log('Trying to annotate: " + positionX + " " + positionY + " " + text + " " + username + "');\n" +
                        "    var div = document.createElement('div');\n" +
                        "    var textContent = document.createTextNode('@" + username + ": " + text + "');\n" +
                        "    div.appendChild(textContent);\n" +
                        "    div.class = 'annotation';\n" +
                        "    div.style.cssText = 'position: absolute; background-color: yellow; height: 60px; width: 120px; z-index: 1;';\n" +
                        "    div.style.top = '" + positionY + "';\n" +
                        "    div.style.left = '" + positionX + "';\n" +
                        "    body.appendChild(div);\n", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {
                                    Log.d(TAG, "YAY! " + s);
                                }
                });
            } catch (Exception e) {
                Log.d(TAG, "Error getting user: ", e);
            }

    }



    public class AnnotationInterface {
        Context mContext;
        int positionX;
        int positionY;

        AnnotationInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void sendCoords(String X, String Y) {
            this.positionX = Integer.valueOf(X);
            this.positionY = Integer.valueOf(Y);

            Log.d(TAG, "SENT THESE COORDS: " + X + ", " + Y);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((BrowserActivity) mContext).setUpAnnotation(positionX, positionY);
                }
            });

        }

    }

}
