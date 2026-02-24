package com.slymax.webview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

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
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webView.setFocusable(true);
        webView.requestFocus();

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectTVMode();
            }
        });

        webView.loadUrl("http://172.16.50.4/");
    }

    private void injectTVMode() {

        String js =
                "(function() {" +

                // 🔥 Disable all existing keyboard handlers
                "document.onkeydown = null;" +
                "document.onkeyup = null;" +
                "window.onkeydown = null;" +
                "window.onkeyup = null;" +

                // Remove header & side panels
                "var header = document.querySelector('header'); if(header) header.remove();" +
                "var topbar = document.getElementById('topbar'); if(topbar) topbar.remove();" +
                "var sidebar = document.getElementById('sidebar'); if(sidebar) sidebar.remove();" +
                "var tree = document.getElementById('tree'); if(tree) tree.remove();" +

                // Expand content
                "var main = document.querySelector('main'); if(main){ main.style.margin='0'; main.style.width='100%'; }" +
                "var content = document.getElementById('content'); if(content){ content.style.margin='0'; content.style.width='100%'; }" +

                // Disable all native focus
                "var all = document.querySelectorAll('*');" +
                "all.forEach(function(el){ el.tabIndex = -1; el.blur && el.blur(); });" +

                // Fix images full height
                "var imgs = document.querySelectorAll('img');" +
                "imgs.forEach(function(img){" +
                "   img.style.maxHeight='100vh';" +
                "   img.style.width='auto';" +
                "   img.style.display='block';" +
                "   img.style.margin='0 auto';" +
                "});" +

                // NAVIGATION SYSTEM
                "window.buildNavigation = function() {" +
                "   window.tvIndex = 0;" +
                "   window.tvItems = Array.from(document.querySelectorAll('#items li.item > a'));" +
                "   window.updateFocus();" +
                "};" +

                "window.updateFocus = function() {" +
                "  if(!window.tvItems || window.tvItems.length === 0) return;" +
                "  window.tvItems.forEach(function(el){" +
                "     el.style.background='';" +
                "     el.style.boxShadow='';" +
                "     el.style.color='';" +
                "  });" +
                "  var el = window.tvItems[window.tvIndex];" +
                "  if(el) {" +
                "     el.style.background='#2a7fff';" +
                "     el.style.color='#ffffff';" +
                "     el.scrollIntoView({block:'center'});" +
                "  }" +
                "};" +

                "window.moveDown = function() {" +
                "  if(!window.tvItems) return;" +
                "  if(window.tvIndex < window.tvItems.length-1) {" +
                "     window.tvIndex++;" +
                "     window.updateFocus();" +
                "  }" +
                "};" +

                "window.moveUp = function() {" +
                "  if(!window.tvItems) return;" +
                "  if(window.tvIndex > 0) {" +
                "     window.tvIndex--;" +
                "     window.updateFocus();" +
                "  }" +
                "};" +

                "window.selectItem = function() {" +
                "  if(window.tvItems && window.tvItems.length > 0) {" +
                "     window.tvItems[window.tvIndex].click();" +
                "  }" +
                "};" +

                // Rebuild navigation whenever DOM changes
                "var observer = new MutationObserver(function() {" +
                "   window.buildNavigation();" +
                "});" +
                "observer.observe(document.body, { childList: true, subtree: true });" +

                "setTimeout(function(){" +
                "   window.buildNavigation();" +
                "}, 500);" +

                "})();";

        webView.evaluateJavascript(js, null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_DPAD_DOWN:
                webView.evaluateJavascript("moveDown()", null);
                return true;

            case KeyEvent.KEYCODE_DPAD_UP:
                webView.evaluateJavascript("moveUp()", null);
                return true;

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                webView.evaluateJavascript("selectItem()", null);
                return true;

            case KeyEvent.KEYCODE_BACK:
                if (webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                break;
        }

        return super.onKeyDown(keyCode, event);
    }
}
