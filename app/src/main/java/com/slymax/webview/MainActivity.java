package com.slymax.webview;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private View cursorView;

    private float cursorX = 300;
    private float cursorY = 300;

    private final int MOVE_STEP = 50;
    private static final String HOME_URL = "http://172.16.50.4/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout rootLayout = new FrameLayout(this);

        webView = new WebView(this);
        rootLayout.addView(webView);

        // Create circular cursor
        cursorView = new View(this);

        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(Color.parseColor("#80FFFFFF")); // semi transparent white
        circle.setStroke(3, Color.BLACK);

        cursorView.setBackground(circle);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(40, 40);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        cursorView.setLayoutParams(params);

        rootLayout.addView(cursorView);

        setContentView(rootLayout);

        setupWebView();
        updateCursorPosition();
    }

    private void setupWebView() {

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                String lower = url.toLowerCase();

                if (lower.endsWith(".mp4") ||
                    lower.endsWith(".mkv") ||
                    lower.endsWith(".m4v") ||
                    lower.endsWith(".avi")) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "video/*");
                    intent.setPackage("org.videolan.vlc");

                    PackageManager pm = getPackageManager();

                    if (intent.resolveActivity(pm) != null) {
                        startActivity(intent);
                    } else {
                        Intent fallback = new Intent(Intent.ACTION_VIEW);
                        fallback.setDataAndType(Uri.parse(url), "video/*");
                        startActivity(fallback);
                    }

                    return true;
                }

                return false;
            }
        });

        webView.loadUrl(HOME_URL);
    }

    private void updateCursorPosition() {
        cursorView.setX(cursorX);
        cursorView.setY(cursorY);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_DPAD_UP:
                cursorY -= MOVE_STEP;
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                cursorY += MOVE_STEP;
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                cursorX -= MOVE_STEP;
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                cursorX += MOVE_STEP;
                break;

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                simulateClick();
                return true;
        }

        if (cursorX < 0) cursorX = 0;
        if (cursorY < 0) cursorY = 0;

        updateCursorPosition();
        return true;
    }

    private void simulateClick() {

        String js = "javascript:(function() {" +
                "var el = document.elementFromPoint(" +
                cursorX + "," + cursorY + ");" +
                "if(el){ el.click(); }" +
                "})()";

        webView.evaluateJavascript(js, null);
    }
}
