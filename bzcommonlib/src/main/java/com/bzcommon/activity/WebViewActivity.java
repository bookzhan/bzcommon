package com.bzcommon.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.luoye.bzcommonlib.R;

public class WebViewActivity extends AppCompatActivity {
    public static final String KEY_URL = "url";
    public static final String KEY_SHOW_ACTION_BAR = "showActionBar";
    public static final String KEY_PAGE_TITLE = "pageTitle";
    public static final String KEY_SHOW_LOADING = "showLoading";
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent = getIntent();
        String url = intent.getStringExtra(KEY_URL);
        boolean showActionBar = intent.getBooleanExtra(KEY_SHOW_ACTION_BAR, true);
        String pageTitle = intent.getStringExtra(KEY_PAGE_TITLE);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            if (showActionBar) {
                supportActionBar.setDisplayHomeAsUpEnabled(true);
            } else {
                supportActionBar.hide();
            }
            supportActionBar.setTitle(pageTitle);
        }
        boolean showLoading = intent.getBooleanExtra(KEY_SHOW_LOADING, true);
        SwipeRefreshLayout swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setEnabled(showLoading);
        if (showLoading) {
            swipe_refresh_layout.setRefreshing(true);
        }
        swipe_refresh_layout.setOnRefreshListener(() -> webView.reload());

        webView = findViewById(R.id.web_view);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //设置加载进来的页面自适应手机屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (showLoading) {
                    swipe_refresh_layout.setRefreshing(true);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (showLoading) {
                    swipe_refresh_layout.setRefreshing(false);
                }
            }
        });
        webView.loadUrl(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
