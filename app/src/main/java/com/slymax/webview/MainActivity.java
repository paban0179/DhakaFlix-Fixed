package com.slymax.webview;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

    private float cursorX = 400;
    private float cursorY = 400;

    private int baseMoveStep = 30;
    private int accelerationStep = 0;

    private final int CURSOR_SIZE = 40;
    private final int EDGE_SCROLL_MARGIN = 120;
    private final int EDGE_SCROLL_AMOUNT = 150;

    private Handler handler = new Handler();
    private Runnable hideCursorRunnable;

    private static final String HOME_URL = "http://172.16.50.4/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout rootLayout = new FrameLayout(this);

        webView = new WebView(this);
        rootLayout.addView(webView);

        createCursor(rootLayout);

        setContentView(rootLayout);

        setupWebView();
        updateCursorPosition();
        setupCursorAutoHide();
    }

    private void createCursor(FrameLayout rootLayout) {

        cursorView = new View(this);

        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(Color.parseColor("#AAFFFFFF"));
        circle.setStroke(3, Color.BLACK);

        cursorView.setBackground(circle);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(CURSOR_SIZE, CURSOR_SIZE);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        cursorView.setLayoutParams(params);

        rootLayout.addView(cursorView);
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

    private void setupCursorAutoHide() {

        hideCursorRunnable = () -> cursorView.setVisibility(View.INVISIBLE);
    }

    private void showCursor() {

        cursorView.setVisibility(View.VISIBLE);
        handler.removeCallbacks(hideCursorRunnable);
        handler.postDelayed(hideCursorRunnable, 3000); // hide after 3 sec
    }

    private void updateCursorPosition() {

        int maxX = webView.getWidth() - CURSOR_SIZE;
        int maxY = webView.getHeight() - CURSOR_SIZE;

        if (cursorX < 0) cursorX = 0;
        if (cursorY < 0) cursorY = 0;
        if (cursorX > maxX) cursorX = maxX;
        if (cursorY > maxY) cursorY = maxY;

        cursorView.setX(cursorX);
        cursorView.setY(cursorY);

        handleEdgeScrolling();
    }

    private void handleEdgeScrolling() {

        if (cursorY > webView.getHeight() - EDGE_SCROLL_MARGIN) {
            webView.scrollBy(0, EDGE_SCROLL_AMOUNT);
        }

        if (cursorY < EDGE_SCROLL_MARGIN) {
            webView.scrollBy(0, -EDGE_SCROLL_AMOUNT);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        showCursor();

        accelerationStep += 5;
        int moveAmount = baseMoveStep + accelerationStep;

        switch (keyCode) {

            case KeyEvent.KEYCODE_DPAD_UP:
                cursorY -= moveAmount;
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                cursorY += moveAmount;
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
                cursorX -= moveAmount;
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                cursorX += moveAmount;
                break;

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                simulateClick();
                return true;
        }

        updateCursorPosition();
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        accelerationStep = 0;
        return super.onKeyUp(keyCode, event);
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
