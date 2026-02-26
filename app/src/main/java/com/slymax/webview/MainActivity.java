package com.slymax.webview;

import android.content.Intent;
import android.net.Uri;
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

                handler.postDelayed(() -> removePanelsIfNotHome(url), 700);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.endsWith(".mp4") ||
                    url.endsWith(".mkv") ||
                    url.endsWith(".avi") ||
                    url.endsWith(".mov")) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setPackage("org.videolan.vlc");
                    intent.setDataAndType(Uri.parse(url), "video/*");

                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        intent.setPackage(null);
                        startActivity(intent);
                    }

                    return true;
                }

                return false;
            }
        });

        webView.loadUrl("http://172.16.50.4/");
    }

    private void removePanelsIfNotHome(String url) {

        String js =
                "(function() {" +

                // Check if NOT home page
                "if(window.location.pathname !== '/' && window.location.pathname !== '') {" +

                // Completely remove header / topbar
                "var header = document.querySelector('header, #topbar');" +
                "if(header) header.remove();" +

                // Completely remove left sidebar / tree
                "var sidebar = document.querySelector('#sidebar, #tree');" +
                "if(sidebar) sidebar.remove();" +

                "}" +

                // Auto-focus first item (keep navigation smooth)
                "var first = document.querySelector('.item a, tr td.name a');" +
                "if(first) {" +
                "   first.focus();" +
                "   first.scrollIntoView({block:'center'});" +
                "}" +

                // If single image page (poster), fill vertically
                "if(document.images.length === 1) {" +
                "   var img = document.images[0];" +
                "   document.body.style.margin='0';" +
                "   img.style.height='100vh';" +
                "   img.style.width='auto';" +
                "   img.style.display='block';" +
                "   img.style.margin='0 auto';" +
                "}" +

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
