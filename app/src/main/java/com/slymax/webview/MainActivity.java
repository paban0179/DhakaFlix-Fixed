package com.slymax.webview;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private static final String HOME_URL = "http://172.16.50.4/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                String lowerUrl = url.toLowerCase();

                if (lowerUrl.endsWith(".mp4") ||
                    lowerUrl.endsWith(".mkv") ||
                    lowerUrl.endsWith(".m4v") ||
                    lowerUrl.endsWith(".avi")) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "video/*");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    // Try VLC first
                    intent.setPackage("org.videolan.vlc");

                    PackageManager pm = getPackageManager();

                    if (intent.resolveActivity(pm) != null) {
                        startActivity(intent);
                    } else {
                        // Fallback to any video player
                        Intent fallback = new Intent(Intent.ACTION_VIEW);
                        fallback.setDataAndType(Uri.parse(url), "video/*");
                        fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(fallback);
                    }

                    return true;
                }

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                new Handler().postDelayed(() -> {

                    String js =
                        "javascript:(function() {" +

                        // Scroll slightly down
                        "window.scrollTo(0, 150);" +

                        // Get all links
                        "var links = document.querySelectorAll('a');" +

                        "for (var i = 0; i < links.length; i++) {" +
                        "   var rect = links[i].getBoundingClientRect();" +

                        // Only focus elements that are visually below header area
                        "   if (rect.top > 120) {" +
                        "       links[i].focus();" +
                        "       break;" +
                        "   }" +
                        "}" +

                        "})();";

                    view.evaluateJavascript(js, null);

                }, 400);
            }
        });

        webView.loadUrl(HOME_URL);

        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (webView.canGoBack()) {
                            webView.goBack();
                        } else {
                            finish();
                        }
                    }
                });
    }
}
