package com.slymax.webview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private final Handler handler = new Handler();

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
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus();

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                // Apply fix multiple times because h5ai re-renders
                applyFixRepeatedly();
            }
        });

        webView.loadUrl("http://172.16.50.4/");
    }

    private void applyFixRepeatedly() {

        handler.postDelayed(this::forceFocusAndFixMedia, 500);
        handler.postDelayed(this::forceFocusAndFixMedia, 1200);
        handler.postDelayed(this::forceFocusAndFixMedia, 2000);
    }

    private void forceFocusAndFixMedia() {

        String js =
                "(function() {" +

                // Remove focus from top panels
                "document.querySelectorAll('header *, #topbar *, .breadcrumbs *, .powered-by *, nav *').forEach(function(el) {" +
                "   el.setAttribute('tabindex','-1');" +
                "});" +

                // Remove sidebar focus
                "document.querySelectorAll('#sidebar *, #tree *').forEach(function(el) {" +
                "   el.setAttribute('tabindex','-1');" +
                "});" +

                // Focus first folder/file item
                "var first = document.querySelector('.item a, tr td.name a');" +
                "if(first) {" +
                "   first.focus();" +
                "   first.scrollIntoView({block:'center'});" +
                "}" +

                // Make images full screen height
                "document.querySelectorAll('img').forEach(function(img){" +
                "   img.style.maxHeight = '100vh';" +
                "   img.style.width = 'auto';" +
                "   img.style.display = 'block';" +
                "   img.style.margin = '0 auto';" +
                "});" +

                // Enable video autoplay + controls
                "document.querySelectorAll('video').forEach(function(v){" +
                "   v.setAttribute('controls', true);" +
                "   v.setAttribute('autoplay', true);" +
                "   v.play().catch(function(){});" +
                "});" +

                "})();";

        webView.evaluateJavascript(js, null);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
