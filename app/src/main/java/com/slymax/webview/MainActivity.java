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

                    intent.setPackage("org.videolan.vlc");

                    PackageManager pm = getPackageManager();

                    if (intent.resolveActivity(pm) != null) {
                        startActivity(intent);
                    } else {
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

                        // Get all links
                        "var links = document.querySelectorAll('a');" +

                        "var fileLinks = [];" +

                        "for (var i = 0; i < links.length; i++) {" +

                        "   var href = links[i].getAttribute('href');" +

                        "   if (!href) continue;" +

                        // Ignore breadcrumb / parent directory links
                        "   if (href === '../') continue;" +

                        // Ignore top navigation links (usually short href)
                        "   if (href.startsWith('#')) continue;" +

                        // Only keep real directory or file links
                        "   if (href.endsWith('/') || href.includes('.')) {" +
                        "       fileLinks.push(links[i]);" +
                        "   }" +
                        "}" +

                        // Disable focus on all links first
                        "for (var j = 0; j < links.length; j++) {" +
                        "   links[j].setAttribute('tabindex', '-1');" +
                        "}" +

                        // Enable focus only for real file links
                        "for (var k = 0; k < fileLinks.length; k++) {" +
                        "   fileLinks[k].setAttribute('tabindex', '0');" +
                        "}" +

                        // Focus first real file link
                        "if (fileLinks.length > 0) {" +
                        "   fileLinks[0].focus();" +
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
