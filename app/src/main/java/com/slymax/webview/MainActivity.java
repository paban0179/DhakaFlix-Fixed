package com.slymax.webview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private final String HOME_URL = "http://172.16.50.4/";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                if (url.endsWith(".mp4") || url.endsWith(".mkv") || url.endsWith(".m4v")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "video/*");
                    intent.setPackage("org.videolan.vlc");
                    startActivity(intent);
                    return true;
                }

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                injectGalleryScript();
            }
        });

        webView.loadUrl(HOME_URL);
    }

    private void injectGalleryScript() {

        String js = "(function() {"

                + "let list = document.querySelector('#items');"
                + "if(!list) return;"

                + "let folders = document.querySelectorAll('#items li.item.folder');"
                + "if(folders.length === 0) return;"

                + "list.style.display='none';"

                + "let gallery = document.createElement('div');"
                + "gallery.style.display='grid';"
                + "gallery.style.gridTemplateColumns='repeat(auto-fill, minmax(220px,1fr))';"
                + "gallery.style.gap='20px';"
                + "gallery.style.padding='20px';"
                + "gallery.style.background='#111';"

                + "folders.forEach(function(folder){"

                + "let link = folder.querySelector('a');"
                + "if(!link) return;"

                + "let folderUrl = link.href;"
                + "let posterUrl = folderUrl + 'a_AL_.jpg';"

                + "let card = document.createElement('div');"
                + "card.style.textAlign='center';"
                + "card.style.color='white';"
                + "card.style.cursor='pointer';"
                + "card.style.outline='none';"
                + "card.setAttribute('tabindex','0');"

                + "let img = document.createElement('img');"
                + "img.src = posterUrl;"
                + "img.style.width='100%';"
                + "img.style.borderRadius='10px';"
                + "img.style.boxShadow='0 4px 10px rgba(0,0,0,0.6)';"

                + "let title = document.createElement('div');"
                + "title.innerText = link.innerText;"
                + "title.style.marginTop='10px';"
                + "title.style.fontSize='16px';"

                + "card.appendChild(img);"
                + "card.appendChild(title);"

                + "card.addEventListener('click', function(){"
                + "window.location.href = folderUrl;"
                + "});"

                + "card.addEventListener('keydown', function(e){"
                + "if(e.key === 'Enter'){ window.location.href = folderUrl; }"
                + "});"

                + "gallery.appendChild(card);"

                + "});"

                + "document.body.appendChild(gallery);"

                + "})();";

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
