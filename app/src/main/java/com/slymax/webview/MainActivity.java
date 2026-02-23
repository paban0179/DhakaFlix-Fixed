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

        String js =
        "(async function() {" +

        "let list = document.querySelector('#items');" +
        "if(!list) return;" +

        "let folders = document.querySelectorAll('#items li.item.folder');" +
        "if(folders.length === 0) return;" +

        // Check if this level is movie level
        "let movieLevel = false;" +

        "for (let folder of folders) {" +
        "let link = folder.querySelector('a');" +
        "if(!link) continue;" +

        "try {" +
        "let res = await fetch(link.href);" +
        "let html = await res.text();" +
        "if(html.match(/\\.mp4|\\.mkv|\\.m4v/i)) {" +
        "movieLevel = true;" +
        "break;" +
        "}" +
        "} catch(e) {}" +
        "}" +

        "if(!movieLevel) return;" +   // STOP if not movie level

        // Transform into grid
        "list.style.display='grid';" +
        "list.style.gridTemplateColumns='repeat(auto-fill,minmax(160px,1fr))';" +
        "list.style.gap='18px';" +
        "list.style.padding='20px';" +
        "list.style.listStyle='none';" +

        "for (let folder of folders) {" +

        "let link = folder.querySelector('a');" +
        "if(!link) continue;" +

        "let folderUrl = link.href;" +
        "let folderName = link.innerText;" +

        "folder.innerHTML='';" +
        "folder.style.textAlign='center';" +

        "let img = document.createElement('img');" +
        "img.style.width='100%';" +
        "img.style.height='240px';" +
        "img.style.objectFit='cover';" +
        "img.style.borderRadius='10px';" +
        "img.style.background='#222';" +

        // Placeholder first
        "img.src='data:image/svg+xml;charset=UTF-8,'+encodeURIComponent(" +
        "'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"300\" height=\"450\">" +
        "<rect width=\"100%\" height=\"100%\" fill=\"#222\"/>" +
        "<text x=\"50%\" y=\"50%\" dominant-baseline=\"middle\" text-anchor=\"middle\" fill=\"#666\" font-size=\"18\">Loading...</text>" +
        "</svg>');" +

        // Fetch folder to get jpg
        "try {" +
        "let res = await fetch(folderUrl);" +
        "let html = await res.text();" +
        "let parser = new DOMParser();" +
        "let doc = parser.parseFromString(html,'text/html');" +
        "let jpg = doc.querySelector('a[href$=\".jpg\"],a[href$=\".JPG\"]');" +
        "if(jpg) {" +
        "img.src = folderUrl + jpg.getAttribute('href');" +
        "} else {" +
        "img.src='data:image/svg+xml;charset=UTF-8,'+encodeURIComponent(" +
        "'<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"300\" height=\"450\">" +
        "<rect width=\"100%\" height=\"100%\" fill=\"#222\"/>" +
        "<text x=\"50%\" y=\"50%\" dominant-baseline=\"middle\" text-anchor=\"middle\" fill=\"#666\" font-size=\"18\">No Poster</text>" +
        "</svg>');" +
        "}" +
        "} catch(e) {}" +

        "let title = document.createElement('div');" +
        "title.innerText = folderName;" +
        "title.style.color='white';" +
        "title.style.marginTop='8px';" +
        "title.style.fontSize='14px';" +

        "folder.appendChild(img);" +
        "folder.appendChild(title);" +
        "folder.setAttribute('tabindex','0');" +

        "folder.addEventListener('click',function(){" +
        "window.location.href=folderUrl;" +
        "});" +

        "folder.addEventListener('keydown',function(e){" +
        "if(e.key==='Enter'){window.location.href=folderUrl;}" +
        "});" +

        "}" +

        "document.body.style.background='#111';" +

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
