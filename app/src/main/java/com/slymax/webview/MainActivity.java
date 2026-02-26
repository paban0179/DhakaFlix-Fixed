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
    private boolean isLaunchingVLC = false;

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

                String js =
                        "javascript:(function() {" +

                        // Disable header/sidebar focus permanently
                        "document.querySelectorAll('header *, #topbar *, nav *, #sidebar *, .sidebar *, .tree *')" +
                        ".forEach(function(el){ el.setAttribute('tabindex','-1'); el.blur(); });" +

                        // Poster fullscreen immediately
                        "if(document.images.length === 1){" +
                        "document.documentElement.style.margin='0';" +
                        "document.body.style.margin='0';" +
                        "document.body.style.overflow='hidden';" +
                        "var img=document.images[0];" +
                        "img.style.position='fixed';" +
                        "img.style.top='0';" +
                        "img.style.left='50%';" +
                        "img.style.transform='translateX(-50%)';" +
                        "img.style.height='100vh';" +
                        "img.style.width='auto';" +
                        "img.style.objectFit='contain';" +
                        "return;" +
                        "}" +

                        // Force first file focus on load
                        "var first=document.querySelector('.item a, tr td.name a');" +
                        "if(first) first.focus();" +

                        "})();";

                webView.evaluateJavascript(js, null);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                String url = request.getUrl().toString();
                String lower = url.toLowerCase();

                if (lower.endsWith(".mp4") ||
                        lower.endsWith(".mkv") ||
                        lower.endsWith(".avi") ||
                        lower.endsWith(".mov")) {

                    if (isLaunchingVLC) return true;
                    isLaunchingVLC = true;

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "video/*");
                    intent.setPackage("org.videolan.vlc");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

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

    @Override
    protected void onResume() {
        super.onResume();
        isLaunchingVLC = false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_UP) {

            int code = event.getKeyCode();

            if (code == KeyEvent.KEYCODE_DPAD_UP ||
                code == KeyEvent.KEYCODE_DPAD_DOWN ||
                code == KeyEvent.KEYCODE_DPAD_CENTER ||
                code == KeyEvent.KEYCODE_ENTER) {

                String js =
                        "javascript:(function() {" +

                        "var focused=document.activeElement;" +
                        "if(!focused) return;" +

                        "switch(" + code + ") {" +

                        // UP
                        "case 19:" +
                        "var prev=focused.closest('tr')?.previousElementSibling;" +
                        "if(prev) prev.querySelector('a')?.focus();" +
                        "break;" +

                        // DOWN
                        "case 20:" +
                        "var next=focused.closest('tr')?.nextElementSibling;" +
                        "if(next) next.querySelector('a')?.focus();" +
                        "break;" +

                        // ENTER
                        "case 23:" +
                        "case 66:" +
                        "if(focused.tagName==='A') focused.click();" +
                        "break;" +

                        "}" +

                        "})();";

                webView.evaluateJavascript(js, null);
                return true;
            }
        }

        return super.dispatchKeyEvent(event);
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
