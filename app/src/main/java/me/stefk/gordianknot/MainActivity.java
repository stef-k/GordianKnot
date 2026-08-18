package me.stefk.gordianknot;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class MainActivity extends Activity {
    private static final String LOCAL_ORIGIN = "https://offline.gordianknot.local/";
    private static final String SENSITIVE_CLIP_KEY = "android.content.extra.IS_SENSITIVE";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }

        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(false);
        settings.setDatabaseEnabled(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        CookieManager.getInstance().setAcceptCookie(false);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, false);

        webView.setWebViewClient(new LockedWebViewClient());
        webView.addJavascriptInterface(new AndroidBridge(this), "AndroidBridge");
        webView.loadDataWithBaseURL(
                LOCAL_ORIGIN,
                readAsset("index.html"),
                "text/html",
                "UTF-8",
                null
        );
        setSystemBars(isSystemDark());
        setContentView(webView);
    }

    private boolean isSystemDark() {
        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    private void setSystemBars(boolean dark) {
        Window window = getWindow();
        int background = Color.parseColor(dark ? "#0B1020" : "#F3F6FB");
        window.setStatusBarColor(background);
        window.setNavigationBarColor(background);

        int visibility = window.getDecorView().getSystemUiVisibility();
        if (dark) {
            visibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            visibility &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        } else {
            visibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            visibility |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        window.getDecorView().setSystemUiVisibility(visibility);
    }

    private String readAsset(String name) {
        try (InputStream input = getAssets().open(name);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = input.read(buffer)) != -1) {
                output.write(buffer, 0, count);
            }
            return output.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load embedded app", e);
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.removeJavascriptInterface("AndroidBridge");
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    private static final class LockedWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return true;
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }
    }

    public static final class AndroidBridge {
        private final MainActivity activity;

        AndroidBridge(MainActivity activity) {
            this.activity = activity;
        }

        @JavascriptInterface
        public boolean copyToClipboard(String value) {
            if (value == null || value.isEmpty()) {
                return false;
            }

            ClipboardManager clipboard =
                    (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard == null) {
                return false;
            }

            ClipData clip = ClipData.newPlainText("Password", value);
            PersistableBundle extras = new PersistableBundle();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true);
            } else {
                extras.putBoolean(SENSITIVE_CLIP_KEY, true);
            }
            clip.getDescription().setExtras(extras);
            clipboard.setPrimaryClip(clip);
            return true;
        }

        @JavascriptInterface
        public void setTheme(String theme) {
            boolean dark = "dark".equals(theme)
                    || ("system".equals(theme) && activity.isSystemDark());
            activity.runOnUiThread(() -> activity.setSystemBars(dark));
        }
    }
}
