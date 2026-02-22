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
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // ONLY apply the gallery logic if we are inside a year folder
                // Adjust "202" to match your folder naming (covers 2020-2029)
                if (url.contains("/20") || url.contains("/Movies/")) {
                    injectGalleryStyle(view);
                }
            }
        });

        myWebView.loadUrl("https://your-dhakaflix-url.com");
    }

    private void injectGalleryStyle(WebView view) {
        // This JavaScript finds text links and turns them into visual cards
        String js = "javascript:(function() {" +
                "var links = document.getElementsByTagName('a');" +
                "for (var i = 0; i < links.length; i++) {" +
                "  var link = links[i];" +
                "  if (link.href.match(/\\.(mp4|mkv|avi)$/i)) {" +
                "    link.style.display = 'inline-block';" +
                "    link.style.margin = '10px';" +
                "    link.style.width = '140px';" +
                "    link.style.textAlign = 'center';" +
                "    link.style.textDecoration = 'none';" +
                "    link.style.color = '#ffffff';" +
                "    var img = document.createElement('img');" +
                "    img.src = 'https://img.icons8.com/color/96/movie-beginning.png';" + // Default movie icon
                "    img.style.width = '100px';" +
                "    img.style.display = 'block';" +
                "    img.style.margin = '0 auto 5px';" +
                "    link.insertBefore(img, link.firstChild);" +
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
