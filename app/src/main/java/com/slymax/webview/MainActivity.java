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
    private Handler handler = new Handler();
    private String lastUrl = "";

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
        webView.requestFocus();

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                lastUrl = url;
                startUrlWatcher();
                applyFocusFix();
            }
        });

        webView.loadUrl("http://172.16.50.4/");
    }

    private void startUrlWatcher() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String current = webView.getUrl();
                if (current != null && !current.equals(lastUrl)) {
                    lastUrl = current;
                    applyFocusFix();
                }
                handler.postDelayed(this, 800);
            }
        }, 800);
    }

    private void applyFocusFix() {

        String js =
                "(function() {" +

                // Remove only focusability, not content
                "document.querySelectorAll('header a, #topbar a, .powered-by a').forEach(function(el) {" +
                "   el.setAttribute('tabindex','-1');" +
                "});" +

                // Focus first folder/file link
                "var first = document.querySelector('.item a, tr td.name a');" +
                "if(first) {" +
                "   first.focus();" +
                "   first.scrollIntoView({block:'center'});" +
                "}" +

                // Fullscreen posters
                "document.querySelectorAll('img').forEach(function(img){" +
                "   img.style.maxHeight='100vh';" +
                "   img.style.width='auto';" +
                "   img.style.display='block';" +
                "   img.style.margin='0 auto';" +
                "});" +

                // Enable video playback
                "document.querySelectorAll('video').forEach(function(v){" +
                "   v.controls = true;" +
                "   v.autoplay = true;" +
                "   v.load();" +
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
