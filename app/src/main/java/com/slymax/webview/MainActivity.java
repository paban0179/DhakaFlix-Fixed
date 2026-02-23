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
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);

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
                + "gallery.style.gap='24px';"
                + "gallery.style.padding='30px';"
                + "gallery.style.background='#111';"
                + "gallery.style.minHeight='100vh';"

                + "folders.forEach(function(folder){"

                + "let link = folder.querySelector('a');"
                + "if(!link) return;"

                + "let folderUrl = link.href;"

                + "let card = document.createElement('div');"
                + "card.style.textAlign='center';"
                + "card.style.color='white';"
                + "card.style.cursor='pointer';"
                + "card.style.outline='none';"
                + "card.setAttribute('tabindex','0');"

                + "let img = document.createElement('img');"
                + "img.style.width='100%';"
                + "img.style.height='330px';"
                + "img.style.objectFit='cover';"
                + "img.style.borderRadius='12px';"
                + "img.style.boxShadow='0 6px 16px rgba(0,0,0,0.6)';"
                + "img.style.background='#222';"

                // Placeholder
                + "let placeholder = 'data:image/svg+xml;charset=UTF-8,' + encodeURIComponent("
                + "'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"300\" height=\"450\">"
                + "<rect width=\"100%\" height=\"100%\" fill=\"#222\"/>"
                + "<text x=\"50%\" y=\"50%\" dominant-baseline=\"middle\" text-anchor=\"middle\" fill=\"#666\" font-size=\"20\">No Poster</text>"
                + "</svg>'"
                + ");"

                + "img.src = placeholder;"

                // Fetch folder HTML to find .jpg
                + "fetch(folderUrl)"
                + ".then(r => r.text())"
                + ".then(html => {"
                + "let match = html.match(/href=\\\"([^\\\"]+\\.jpg)\\\"/i);"
                + "if(match){"
                + "img.src = folderUrl + match[1];"
                + "}"
                + "})"
                + ".catch(()=>{});"

                + "let title = document.createElement('div');"
                + "title.innerText = link.innerText;"
                + "title.style.marginTop='12px';"
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
