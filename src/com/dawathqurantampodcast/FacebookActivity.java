
package com.dawathqurantampodcast;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.dawathqurantampodcast.R;

public class FacebookActivity extends Activity {
    /** Called when the activity is first created. */
    private WebView facebookweBview;
    Context con;

    String url = AppInfo.facebookFanPageUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.facebookview);
        con = this;

        try {
            updateWebView(url);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private class HelloWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && facebookweBview.canGoBack()) {
            facebookweBview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    private void updateWebView(String url) {
        // TODO Auto-generated method stub

        facebookweBview = (WebView) findViewById(R.id.mapViewface);
        facebookweBview.getSettings().setJavaScriptEnabled(true);
        facebookweBview.getSettings().setDomStorageEnabled(true);
        facebookweBview.loadUrl(url);

        facebookweBview.setWebViewClient(new HelloWebViewClient());

    }
}
