package com.slymax.webview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebResourceRequest;
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

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                handler.postDelayed(() -> injectTVMode(), 600);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                String url = request.getUrl().toString().toLowerCase();

                if (url.endsWith(".mp4") ||
                    url.endsWith(".mkv") ||
                    url.endsWith(".avi") ||
                    url.endsWith(".mov")) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "video/*");
                    intent.setPackage("org.videolan.vlc");

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

    private void injectTVMode() {

        String js =
                "(function() {" +

                "if(window.location.pathname !== '/' && window.location.pathname !== '') {" +

                "if(!document.getElementById('tv-style')) {" +
                "var style=document.createElement('style');" +
                "style.id='tv-style';" +
                "style.innerHTML='" +

                "header,#topbar,.topbar,.header{display:none!important;}" +
                "#sidebar,#tree,.sidebar,.tree,nav{display:none!important;}" +
                "main,.content,#content{width:100%!important;margin:0!important;}" +

                "';" +
                "document.head.appendChild(style);" +
                "}" +

                "}" +

                // Poster fullscreen
                "if(document.images.length===1){" +
                "var img=document.images[0];" +
                "document.body.style.margin='0';" +
                "img.style.height='100vh';" +
                "img.style.width='auto';" +
                "img.style.display='block';" +
                "img.style.margin='0 auto';" +
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
