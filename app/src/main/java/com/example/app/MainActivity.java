package com.slymax.webview;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = findViewById(R.id.webview);
        WebSettings settings = myWebView.getSettings();
        
        // --- Essential Features ---
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true); // REQUIRED for search boxes to work
        settings.setDatabaseEnabled(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        // --- Browser Identity ---
        // This stops the website from redirecting you away from the app
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // HYBRID LOGIC: Only apply visual gallery if URL contains a year (like /2024/)
                // This keeps your homepage "standard" and intact.
                if (url.contains("/20")) {
                    injectGalleryStyle(view);
                }
            }
        });

        // LOAD DHAKAFLIX
        myWebView.loadUrl("https://your-dhakaflix-url.com");
    }

    private void injectGalleryStyle(WebView view) {
        // This JavaScript targets only .mp4/mkv links to show an icon box
        String js = "javascript:(function() {" +
                "var links = document.getElementsByTagName('a');" +
                "for (var i = 0; i < links.length; i++) {" +
                "  var link = links[i];" +
                "  if (link.href.match(/\\.(mp4|mkv|avi)$/i)) {" +
                "    link.style.display = 'inline-block';" +
                "    link.style.margin = '10px';" +
                "    link.style.padding = '10px';" +
                "    link.style.width = '120px';" +
                "    link.style.backgroundColor = '#1a1a1a';" +
                "    link.style.borderRadius = '8px';" +
                "    link.style.textAlign = 'center';" +
                "    link.style.textDecoration = 'none';" +
                "    link.style.color = '#ffffff';" +
                "    var img = document.createElement('img');" +
                "    img.src = 'https://img.icons8.com/color/96/movie-beginning.png';" +
                "    img.style.width = '80px';" +
                "    img.style.display = 'block';" +
                "    img.style.margin = '0 auto 8px';" +
                "    if (!link.querySelector('img')) {" +
                "        link.insertBefore(img, link.firstChild);" +
                "    }" +
                "  }" +
                "}" +
                "})()";
        view.loadUrl(js);
    }

    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack(); // Back button stays in website
        } else {
            super.onBackPressed();
        }
    }
}
