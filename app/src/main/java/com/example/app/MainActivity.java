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
        
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        
        // This ensures the server treats the app as a mobile browser
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36");

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Injects the visual movie gallery script once the page loads
                injectGalleryStyle(view);
            }
        });

        // YOUR CORRECT MEDIA SERVER URL
        myWebView.loadUrl("http://172.16.50.4/");
    }

    private void injectGalleryStyle(WebView view) {
        String js = "javascript:(function() {" +
                "var links = document.getElementsByTagName('a');" +
                "for (var i = 0; i < links.length; i++) {" +
                "  var link = links[i];" +
                "  if (link.href.match(/\\.(mp4|mkv|avi|mov)$/i)) {" +
                "    link.style.display = 'inline-block';" +
                "    link.style.margin = '10px';" +
                "    link.style.padding = '10px';" +
                "    link.style.width = '120px';" +
                "    link.style.backgroundColor = '#222222';" +
                "    link.style.borderRadius = '12px';" +
                "    link.style.textAlign = 'center';" +
                "    link.style.textDecoration = 'none';" +
                "    link.style.color = '#ffffff';" +
                "    link.style.fontSize = '12px';" +
                "    var img = document.createElement('img');" +
                "    img.src = 'https://img.icons8.com/color/96/movie-beginning.png';" +
                "    img.style.width = '70px';" +
                "    img.style.display = 'block';" +
                "    img.style.margin = '0 auto 8px';" +
                "    if (!link.querySelector('img')) link.insertBefore(img, link.firstChild);" +
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
                "    if (!link.querySelector('img')) link.insertBefore(img, link.firstChild);" +
                "  }" +
                "}" +
                "})()";
        view.loadUrl(js);
    }

    @Override
    public void onBackPressed() {
        if (myWebView != null && myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
