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
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus();

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {

            // 🔥 Redirect video files to VLC
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

                // Run multiple passes to defeat h5ai DOM rebuild
                handler.postDelayed(() -> forceFolderFocus(), 600);
                handler.postDelayed(() -> forceFolderFocus(), 1800);
                handler.postDelayed(() -> forceFolderFocus(), 3200);
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

            return true; // prevent WebView from loading video
        }

        return false;
    }

    private void forceFolderFocus() {

        String js =
                "(function() {" +

                // Disable focus on top panel
                "document.querySelectorAll('header *, #topbar *, .breadcrumbs *, .powered-by *').forEach(function(el) {" +
                "   el.setAttribute('tabindex','-1');" +
                "});" +

                // Disable focus on sidebar
                "document.querySelectorAll('#sidebar *, #tree *').forEach(function(el) {" +
                "   el.setAttribute('tabindex','-1');" +
                "});" +

                // Focus first folder/file item
                "var first = document.querySelector('.item a, tr td.name a');" +
                "if(first) {" +
                "   first.focus();" +
                "   first.scrollIntoView({block:'center'});" +
                "}" +

                // Make poster images fill TV vertically
                "document.querySelectorAll('img').forEach(function(img) {" +
                "   img.style.maxHeight = '100vh';" +
                "   img.style.width = 'auto';" +
                "   img.style.objectFit = 'contain';" +
                "});" +

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
}
