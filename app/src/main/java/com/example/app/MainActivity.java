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

    private float cursorX = 300f;
    private float cursorY = 300f;

    private final float MOVE_STEP = 14f;
    private final int CURSOR_SIZE = 36;

    private final int EDGE_MARGIN = 80;
    private final int SCROLL_AMOUNT = 120;

    private Handler handler = new Handler();
    private Runnable hideCursorRunnable;

    private static final String HOME_URL = "http://172.16.50.4/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout root = new FrameLayout(this);

        webView = new WebView(this);
        root.addView(webView);

        createCursor(root);

        setContentView(root);

        setupWebView();
        setupCursorAutoHide();

        // Prevent WebView focus stealing
        webView.setFocusable(false);
        webView.setFocusableInTouchMode(false);

        updateCursorPosition();
    }

    private void createCursor(FrameLayout root) {

        cursorView = new View(this);

        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(Color.parseColor("#CCFFFFFF"));
        circle.setStroke(2, Color.BLACK);

        cursorView.setBackground(circle);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(CURSOR_SIZE, CURSOR_SIZE);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        cursorView.setLayoutParams(params);

        root.addView(cursorView);
    }

    private void setupWebView() {

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
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
        handler.postDelayed(hideCursorRunnable, 4000);
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

        handleEdgeScroll();
    }

    private void handleEdgeScroll() {

        if (cursorY > webView.getHeight() - EDGE_MARGIN) {
            webView.scrollBy(0, SCROLL_AMOUNT);
        }

        if (cursorY < EDGE_MARGIN) {
            webView.scrollBy(0, -SCROLL_AMOUNT);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            showCursor();

            switch (event.getKeyCode()) {

                case KeyEvent.KEYCODE_DPAD_UP:
                    cursorY -= MOVE_STEP;
                    updateCursorPosition();
                    return true;

                case KeyEvent.KEYCODE_DPAD_DOWN:
                    cursorY += MOVE_STEP;
                    updateCursorPosition();
                    return true;

                case KeyEvent.KEYCODE_DPAD_LEFT:
                    cursorX -= MOVE_STEP;
                    updateCursorPosition();
                    return true;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    cursorX += MOVE_STEP;
                    updateCursorPosition();
                    return true;

                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    simulateClick();
                    return true;

                case KeyEvent.KEYCODE_BACK:
                    handleBack();
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    private void handleBack() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    private void simulateClick() {

        float scale = webView.getScale();

        float scrollX = webView.getScrollX();
        float scrollY = webView.getScrollY();

        float adjustedX = (cursorX + scrollX) / scale;
        float adjustedY = (cursorY + scrollY) / scale;

        String js = "javascript:(function() {" +
                "var el = document.elementFromPoint(" +
                adjustedX + "," + adjustedY + ");" +
                "if(el){ el.click(); }" +
                "})()";

        webView.evaluateJavascript(js, null);
    }
}
