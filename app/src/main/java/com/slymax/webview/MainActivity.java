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
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus();

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectCleanTVMode();
            }
        });

        webView.loadUrl("http://172.16.50.4/");
    }

    private void injectCleanTVMode() {

        String js =
                "(function() {" +

                // 1️⃣ Remove top horizontal bar completely
                "var header = document.querySelector('header');" +
                "if(header) header.remove();" +

                "var topbar = document.getElementById('topbar');" +
                "if(topbar) topbar.remove();" +

                // 2️⃣ Remove left directory tree completely
                "var sidebar = document.getElementById('sidebar');" +
                "if(sidebar) sidebar.remove();" +

                "var tree = document.getElementById('tree');" +
                "if(tree) tree.remove();" +

                // 3️⃣ Expand main content to full width
                "var main = document.querySelector('main');" +
                "if(main) {" +
                "  main.style.margin = '0';" +
                "  main.style.width = '100%';" +
                "}" +

                // Some h5ai versions use #content
                "var content = document.getElementById('content');" +
                "if(content) {" +
                "  content.style.margin = '0';" +
                "  content.style.width = '100%';" +
                "}" +

                // 4️⃣ Focus only folder list
                "function setupNavigation() {" +
                "  var items = Array.from(document.querySelectorAll('#items li.item > a'));" +
                "  if(items.length === 0) return;" +

                "  items.forEach(function(el, i) {" +
                "    el.tabIndex = 0;" +
                "    el.dataset.index = i;" +
                "  });" +

                "  setTimeout(function() {" +
                "    items[0].focus();" +
                "    items[0].scrollIntoView({block:'center'});" +
                "  }, 300);" +

                "  document.addEventListener('keydown', function(e) {" +
                "    var active = document.activeElement;" +
                "    if(!active || !active.dataset.index) return;" +
                "    var index = parseInt(active.dataset.index);" +

                // DOWN
                "    if(e.keyCode === 40) {" +
                "      e.preventDefault();" +
                "      if(index < items.length - 1) {" +
                "        items[index + 1].focus();" +
                "        items[index + 1].scrollIntoView({block:'center'});" +
                "      }" +
                "    }" +

                // UP
                "    if(e.keyCode === 38) {" +
                "      e.preventDefault();" +
                "      if(index > 0) {" +
                "        items[index - 1].focus();" +
                "        items[index - 1].scrollIntoView({block:'center'});" +
                "      }" +
                "    }" +

                // ENTER
                "    if(e.keyCode === 13) {" +
                "      e.preventDefault();" +
                "      active.click();" +
                "    }" +
                "  });" +
                "}" +

                // Delay to override h5ai rendering
                "setTimeout(setupNavigation, 500);" +

                "})();";

        webView.evaluateJavascript(js, null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
