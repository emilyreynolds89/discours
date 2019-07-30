package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrowserActivity extends AppCompatActivity {

    private static final String TAG = "BrowserActivity";

    public @BindView(R.id.webview) WebView webView;
    public @BindView(R.id.toolbar) Toolbar toolbar;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "yes we got to this activity");

        setContentView(R.layout.activity_browser);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        url = getIntent().getStringExtra("url");

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl(url);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        return true;
    }


}
