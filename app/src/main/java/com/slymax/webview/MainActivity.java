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

        // Enable proper zoom + scaling
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

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
        "(function() {" +

        "let list = document.querySelector('#items');" +
        "if(!list) return;" +

        "let folders = document.querySelectorAll('#items li.item.folder');" +
        "let videos = document.querySelectorAll('#items li.item.file a[href$=\".mp4\"], #items li.item.file a[href$=\".mkv\"], #items li.item.file a[href$=\".m4v\"]');" +

        // Only transform if page has folders AND does NOT contain video files
        "if(folders.length === 0 || videos.length > 0) return;" +

        // White background
        "document.body.style.background = '#ffffff';" +

        // Clean responsive grid (2 posters per row on phone, 3 on larger)
        "list.style.display='grid';" +
        "list.style.gridTemplateColumns='repeat(auto-fill, minmax(160px, 1fr))';" +
        "list.style.gap='16px';" +
        "list.style.padding='16px';" +
        "list.style.listStyle='none';" +

        "folders.forEach(function(folder) {" +

        "let link = folder.querySelector('a');" +
        "if(!link) return;" +

        "let folderUrl = link.href;" +
        "let folderName = link.textContent.trim();" +

        "folder.innerHTML='';" +
        "folder.style.textAlign='center';" +

        "let img = document.createElement('img');" +
        "img.style.width='100%';" +
        "img.style.aspectRatio='2/3';" +
        "img.style.objectFit='contain';" +
        "img.style.borderRadius='8px';" +
        "img.style.background='#f0f0f0';" +
        "img.style.maxHeight='260px';" +

        // Fetch folder HTML and extract first JPG
        "fetch(folderUrl)" +
        ".then(r=>r.text())" +
        ".then(html=>{" +
        "let parser = new DOMParser();" +
        "let doc = parser.parseFromString(html, 'text/html');" +
        "let jpg = doc.querySelector('a[href$=\".jpg\"], a[href$=\".JPG\"]');" +
        "if(jpg) {" +
        "img.src = folderUrl + jpg.getAttribute('href');" +
        "} else {" +
        "img.src = '';" +
        "}" +
        "})" +
        ".catch(()=>{ img.src=''; });" +

        "let title = document.createElement('div');" +
        "title.textContent = folderName;" +
        "title.style.marginTop='8px';" +
        "title.style.fontSize='14px';" +
        "title.style.color='#000';" +
        "title.style.wordBreak='break-word';" +

        "folder.appendChild(img);" +
        "folder.appendChild(title);" +

        "folder.onclick=function(){ window.location.href = folderUrl; };" +

        "});" +

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
