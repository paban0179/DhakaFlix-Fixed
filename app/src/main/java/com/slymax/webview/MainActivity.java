package com.slymax.webview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
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

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus();

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                // Wait 800ms to let h5ai fully initialize
                handler.postDelayed(() -> forceFolderFocus(), 800);
            }
        });

        webView.loadUrl("http://172.16.50.4/");
    }

    private void forceFolderFocus() {

        String js =
                "(function() {" +

                // Remove focus from top panel elements
                "document.querySelectorAll('header *, #topbar *, .breadcrumbs *, .powered-by *').forEach(function(el) {" +
                "   el.setAttribute('tabindex','-1');" +
                "});" +

                // Remove focus from sidebar
                "document.querySelectorAll('#sidebar *, #tree *').forEach(function(el) {" +
                "   el.setAttribute('tabindex','-1');" +
                "});" +

                // Focus first actual folder/file link
                "var first = document.querySelector('.item a, tr td.name a');" +
                "if(first) {" +
                "   first.focus();" +
                "   first.scrollIntoView({block:'center'});" +
                "}" +

                "})();";

        webView.evaluateJavascript(js, null);
    }

    // Proper BACK handling
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // Optional: ensure DPAD works normally
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
