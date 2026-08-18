package com.stef.offlinepassgen;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new AndroidBridge(this), "AndroidBridge");

        // The page is bundled in the APK. Giving it an HTTPS base URL enables
        // Web Crypto APIs without making any network request.
        String html = readAsset("index.html");
        webView.loadDataWithBaseURL(
                "https://offline.passgen.local/",
                html,
                "text/html",
                "UTF-8",
                null
        );

        setContentView(webView);
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

    public static final class AndroidBridge {
        private final Context context;

        AndroidBridge(Context context) {
            this.context = context.getApplicationContext();
        }

        @JavascriptInterface
        public void copyToClipboard(String value) {
            ClipboardManager clipboard =
                    (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

            if (clipboard != null) {
                clipboard.setPrimaryClip(ClipData.newPlainText("Password", value));
            }
        }
    }
}
