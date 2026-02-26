package com.slymax.webview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

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

    // 🔥 CAPTURE DPAD KEYS
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_UP) {

            int code = event.getKeyCode();

            switch (code) {

                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:

                    injectKeyIntoWebView(code);
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    // 🔥 SEND KEY TO WEB PAGE
    private void injectKeyIntoWebView(int keyCode) {

        String js =
            "javascript:(function() {" +
            "if(!window.androidNativeKey) {" +
            "window.androidNativeKey = function(code) {" +

            "var focused = document.activeElement;" +

            "switch(code) {" +

            // UP
            "case 19:" +
            "  var prev = focused.parentElement.previousElementSibling;" +
            "  if(prev) prev.querySelector('a')?.focus();" +
            "  break;" +

            // DOWN
            "case 20:" +
            "  var next = focused.parentElement.nextElementSibling;" +
            "  if(next) next.querySelector('a')?.focus();" +
            "  break;" +

            // LEFT
            "case 21:" +
            "  focused.blur();" +
            "  break;" +

            // RIGHT
            "case 22:" +
            "  focused.focus();" +
            "  break;" +

            // ENTER / CENTER
            "case 23:" +
            "case 66:" +
            "  focused.click();" +
            "  break;" +

            "}" +
            "};" +
            "}" +

            "window.androidNativeKey(" + keyCode + ");" +
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
