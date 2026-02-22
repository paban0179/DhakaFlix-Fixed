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
        
        // --- Core Settings ---
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true); // Fixes search box functionality
        settings.setDatabaseEnabled(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        // --- Fix Redirects & Compatibility ---
        // This makes the website treat the app like a real Chrome browser
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Keep navigation inside the app
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // ONLY transform links if we are inside a year folder (e.g., /2024/, /2023/)
                if (url.contains("/20")) {
                    injectGalleryStyle(view);
                }
            }
        });

        // Use your actual website URL here
        myWebView.loadUrl("https://your-dhakaflix-url.com");
    }

    private void injectGalleryStyle(WebView view) {
        // This script turns .mp4/.mkv links into visual boxes only in subfolders
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
                "    link.style.fontSize = '12px';" +
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
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
