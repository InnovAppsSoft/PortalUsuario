package com.marlon.portalusuario.activities;

import android.os.Bundle;

import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.marlon.portalusuario.R;


public class PrivacyActivity extends AppCompatActivity {

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        WebView myWebView = findViewById(R.id.webview_privacy);
        myWebView.loadUrl("https://sites.google.com/view/marlondejesusmilanesrivero/pol%C3%ADtica-de-privacidad");

    }
}
