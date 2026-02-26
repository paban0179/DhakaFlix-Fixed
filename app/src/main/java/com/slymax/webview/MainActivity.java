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

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus();

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                injectTVGrid();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                String url = request.getUrl().toString().toLowerCase();

                if (url.endsWith(".mp4") || url.endsWith(".mkv") ||
                    url.endsWith(".avi") || url.endsWith(".mov")) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setPackage("org.videolan.vlc");
                    intent.setDataAndType(Uri.parse(url), "video/*");

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

    private void injectTVGrid() {

        String js =
                "(function() {" +

                "document.body.innerHTML = '';" +
                "document.body.style.margin='0';" +
                "document.body.style.background='#000';" +

                "var links = Array.from(document.getElementsByTagName('a'));" +

                "var grid = document.createElement('div');" +
                "grid.style.display='grid';" +
                "grid.style.gridTemplateColumns='repeat(3, 1fr)';" +
                "grid.style.gap='20px';" +
                "grid.style.padding='40px';" +
                "grid.style.minHeight='100vh';" +

                "var items = {};" +

                "links.forEach(function(l) {" +
                "  var name = l.innerText.trim();" +
                "  if(!name || name.includes('Parent')) return;" +

                "  var clean = name.replace(/\\.[^/.]+$/, '').trim();" +
                "  if(!items[clean]) items[clean] = {name:clean, url:l.href, type:'folder'};" +

                "  if(l.href.match(/\\.(mp4|mkv|avi|mov)$/i)) {" +
                "     items[clean].url = l.href;" +
                "     items[clean].type = 'video';" +
                "  }" +

                "  if(l.href.match(/\\.(jpg|jpeg|png)$/i)) {" +
                "     items[clean].img = l.href;" +
                "  }" +
                "});" +

                "for(var key in items) {" +
                "  var item = items[key];" +

                "  var card = document.createElement('a');" +
                "  card.href = item.url;" +
                "  card.tabIndex = 0;" +
                "  card.style.textDecoration='none';" +
                "  card.style.outline='none';" +

                "  var wrapper = document.createElement('div');" +
                "  wrapper.style.background='#111';" +
                "  wrapper.style.borderRadius='12px';" +
                "  wrapper.style.overflow='hidden';" +
                "  wrapper.style.transition='transform 0.2s, box-shadow 0.2s';" +

                "  wrapper.onfocus = function() {" +
                "     this.style.transform='scale(1.08)';" +
                "     this.style.boxShadow='0 0 25px #00aaff';" +
                "  };" +

                "  wrapper.onblur = function() {" +
                "     this.style.transform='scale(1)';" +
                "     this.style.boxShadow='none';" +
                "  };" +

                "  var img = document.createElement('img');" +
                "  img.src = item.img || 'https://via.placeholder.com/300x450?text=Folder';" +
                "  img.style.width='100%';" +
                "  img.style.height='70vh';" +   // FULL VERTICAL TV SIZE
                "  img.style.objectFit='cover';" +

                "  var title = document.createElement('div');" +
                "  title.innerText = item.name;" +
                "  title.style.color='white';" +
                "  title.style.padding='10px';" +
                "  title.style.textAlign='center';" +
                "  title.style.fontSize='18px';" +

                "  wrapper.appendChild(img);" +
                "  wrapper.appendChild(title);" +
                "  card.appendChild(wrapper);" +
                "  grid.appendChild(card);" +
                "}" +

                "document.body.appendChild(grid);" +

                "setTimeout(function() {" +
                "  var first = document.querySelector('a');" +
                "  if(first) first.focus();" +
                "}, 300);" +

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
