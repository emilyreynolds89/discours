package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;

import com.codepath.fbu_newsfeed.Models.Article;
import com.parse.ParseUser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrowserActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BrowserActivity";

    public @BindView(R.id.webview) WebView webView;
    public @BindView(R.id.toolbar) Toolbar toolbar;
    public @BindView(R.id.btnShare)
    Button btnShare;
    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.ibForward) ImageButton ibForward;
    @BindView(R.id.ibRefresh) ImageButton ibRefresh;

    private String url;
    private Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browser);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        article = (Article) getIntent().getSerializableExtra("article");
        url = article.getUrl();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
