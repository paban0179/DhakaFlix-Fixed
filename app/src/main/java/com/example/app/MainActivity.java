package com.slymax.webview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.SearchView; // Using standard Android widget to avoid conflict

public class MainActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webview);
        SearchView searchView = (SearchView) findViewById(R.id.searchView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String script = "javascript:(function() {" +
                    "var links = Array.from(document.getElementsByTagName('a'));" +
                    "var container = document.createElement('div');" +
                    "container.id = 'dhakaflix-grid';" +
                    "container.style = 'display:grid; grid-template-columns:repeat(2, 1fr); gap:10px; padding:10px; background:#121212; min-height:100vh;';" +
                    "var items = {};" +
                    "links.forEach(l => {" +
                    "  var text = l.innerText.trim();" +
                    "  var name = text.replace(/\\.[^/.]+$/, '').trim();" +
                    "  if(!items[name]) items[name] = {name:name, url:l.href, type:'folder'};" +
                    "  if(l.href.endsWith('.mp4') || l.href.endsWith('.mkv')) { items[name].url=l.href; items[name].type='video'; }" +
                    "  if(l.href.endsWith('.jpg')) items[name].img = l.href;" +
                    "});" +
                    "for(var k in items) {" +
                    "  var item = items[k]; if(item.name.includes('Parent') || !item.name) continue;" +
                    "  var card = document.createElement('div');" +
                    "  card.className = 'movie-card';" +
                    "  card.innerHTML = '<img src=\"'+(item.img || 'https://via.placeholder.com/150x220?text=Folder')+'\" style=\"width:100%; border-radius:8px; aspect-ratio:2/3; object-fit:cover;\"><p style=\"color:white; text-align:center; font-size:12px; margin-top:5px;\">'+item.name+'</p>';" +
                    "  card.onclick = function(u, t){ return function(){ window.location.href = (t==='video' ? 'vlc://' + u : u); }; }(item.url, item.type);" +
                    "  container.appendChild(card);" +
                    "}" +
                    "document.body.innerHTML = ''; document.body.style.margin='0'; document.body.appendChild(container);" +
                    "})()";
                view.evaluateJavascript(script, null);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.endsWith(".mp4") || url.endsWith(".mkv") || url.startsWith("vlc://")) {
                    String cleanUrl = url.replace("vlc://", "");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setPackage("org.videolan.vlc");
                    intent.setDataAndType(Uri.parse(cleanUrl), "video/*");
                    try { startActivity(intent); } 
                    catch (Exception e) { intent.setPackage(null); startActivity(intent); }
                    return true;
                }
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                String filter = "javascript:(function() {" +
                    "var cards = document.getElementsByClassName('movie-card');" +
                    "var q = '" + newText.toLowerCase() + "';" +
                    "for(var i=0; i<cards.length; i++) {" +
                    "  var name = cards[i].innerText.toLowerCase();" +
                    "  cards[i].style.display = name.includes(q) ? 'block' : 'none';" +
                    "}" +
                    "})()";
                webView.evaluateJavascript(filter, null);
                return true;
            }
        });

        webView.loadUrl("http://172.16.50.4/");
    }
}
