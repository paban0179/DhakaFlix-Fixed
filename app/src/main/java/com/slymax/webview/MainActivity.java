package com.slymax.webview;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
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
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectTVNavigation();
            }
        });

        webView.loadUrl("http://YOUR_SERVER_URL_HERE/");
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

    private void injectTVNavigation() {

        String js = 
            "(function() {" +

            // Remove topbar and sidebar completely
            "var topbar = document.getElementById('topbar');" +
            "if (topbar) topbar.style.display='none';" +

            "var sidebar = document.getElementById('sidebar');" +
            "if (sidebar) sidebar.style.display='none';" +

            "var tree = document.getElementById('tree');" +
            "if (tree) tree.style.display='none';" +

            // Remove all existing tabindex
            "document.querySelectorAll('*').forEach(function(el){" +
                "el.removeAttribute('tabindex');" +
            "});" +

            // Get only folder and file items
            "var items = document.querySelectorAll('#items li.item > a');" +
            "if (!items.length) return;" +

            "var index = 0;" +

            "function clearSelection(){" +
                "items.forEach(function(el){" +
                    "el.style.background='white';" +
                    "el.style.outline='none';" +
                "});" +
            "}" +

            "function selectItem(i){" +
                "if (i < 0) i = 0;" +
                "if (i >= items.length) i = items.length - 1;" +
                "index = i;" +
                "clearSelection();" +
                "items[index].style.background='#E3F2FD';" +
                "items[index].style.outline='3px solid #2196F3';" +
                "items[index].scrollIntoView({behavior:'auto', block:'center'});" +
            "}" +

            "selectItem(0);" +

            "document.addEventListener('keydown', function(e){" +

                // DOWN
                "if (e.keyCode == 40){" +
                    "e.preventDefault();" +
                    "selectItem(index + 1);" +
                "}" +

                // UP
                "if (e.keyCode == 38){" +
                    "e.preventDefault();" +
                    "selectItem(index - 1);" +
                "}" +

                // ENTER or DPAD_CENTER
                "if (e.keyCode == 13 || e.keyCode == 23){" +
                    "e.preventDefault();" +
                    "window.location = items[index].href;" +
                "}" +

            "});" +

            "})();";

        webView.evaluateJavascript(js, null);
    }
}
