package com.example.rss2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

public class MainActivity2 extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        webView = findViewById(R.id.web);
        Intent intent = getIntent();
        String url = intent.getStringExtra("URL");

        webView.loadUrl(String.valueOf(url));
    }
}