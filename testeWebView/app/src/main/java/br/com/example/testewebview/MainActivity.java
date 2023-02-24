package br.com.example.testewebview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView wvNav = (WebView) findViewById(R.id.wvNavega);
        /////// Substitua o link da TECTOY pelo seu link
        wvNav.loadUrl("https://www.tectoy.com.br/");
        WebSettings webSettings = wvNav.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wvNav.setWebViewClient(new WebViewClient());

    }
}