package com.slymax.webview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
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

            // 🔥 VLC redirect
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleVideoRedirect(request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleVideoRedirect(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                handler.postDelayed(() -> cleanPage(url), 800);
            }
        });

        webView.loadUrl("http://172.16.50.4/");
    }

    // 🔥 VLC REDIRECT
    private boolean handleVideoRedirect(String url) {

        if (url == null) return false;

        String lower = url.toLowerCase();

        if (lower.endsWith(".mp4") ||
            lower.endsWith(".mkv") ||
            lower.endsWith(".avi") ||
            lower.endsWith(".mov") ||
            lower.endsWith(".wmv") ||
            lower.endsWith(".flv") ||
            lower.endsWith(".webm") ||
            lower.endsWith(".m4v")) {

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "video/*");
                intent.setPackage("org.videolan.vlc");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "video/*");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            return true;
        }

        return false;
    }

    private void cleanPage(String url) {

        String js =
                "(function() {" +

                // Remove top panel and sidebar EXCEPT on homepage
                "if(window.location.pathname !== '/' && window.location.pathname !== '') {" +

                "var top = document.querySelector('header, #topbar');" +
                "if(top) top.remove();" +

                "var side = document.querySelector('#sidebar, #tree');" +
                "if(side) side.remove();" +

                "}" +

                // Focus first folder/file
                "var first = document.querySelector('.item a, tr td.name a');" +
                "if(first) {" +
                "   first.focus();" +
                "   first.scrollIntoView({block:'center'});" +
                "}" +

                // Fullscreen posters vertically
                "document.querySelectorAll('img').forEach(function(img) {" +
                "   img.style.maxHeight = '100vh';" +
                "   img.style.width = 'auto';" +
                "   img.style.objectFit = 'contain';" +
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
