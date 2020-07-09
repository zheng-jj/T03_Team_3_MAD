package com.example.t03team3mad;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class ViewDemoBook extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        webView = (WebView) findViewById(R.id.demobook);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                return false;
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }else {
            CookieManager.getInstance().setAcceptCookie(true);
        }

        webView.loadUrl("http://books.google.com.sg/books?id=rBd4BAAAQBAJ&printsec=frontcover&dq=isbn:9781908843708&hl=&cd=1&source=gbs_api");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
