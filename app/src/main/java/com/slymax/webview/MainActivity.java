package com.slymax.webview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        // Keep normal focus behavior
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus();

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                fixFocusTrap();
            }
        });

        webView.loadUrl("http://172.16.50.4/");
    }

    private void fixFocusTrap() {

        String js =
                "(function() {" +

                // Remove focus from top panel elements
                "document.querySelectorAll('header a, #topbar a, .breadcrumbs a, .powered-by a').forEach(function(el) {" +
                "    el.setAttribute('tabindex', '-1');" +
                "});" +

                // Remove focus from sidebar tree if present
                "document.querySelectorAll('#sidebar a, #tree a').forEach(function(el) {" +
                "    el.setAttribute('tabindex', '-1');" +
                "});" +

                // Focus first folder/file row
                "var firstItem = document.querySelector('.item a, tr td.name a');" +
                "if (firstItem) {" +
                "    firstItem.focus();" +
                "}" +

                "})();";

        webView.evaluateJavascript(js, null);
    }
}
