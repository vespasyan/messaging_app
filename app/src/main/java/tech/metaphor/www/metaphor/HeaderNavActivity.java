package tech.metaphor.www.metaphor;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;


public class HeaderNavActivity extends AppCompatActivity {

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header);
/*
        WebView webView2 = findViewById(R.id.webView2);
        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.addJavascriptInterface(new HeaderNavActivity(), "injectedObject");
        webView2.loadUrl("file:///android_asset/www/slow_motion_particles.html");

*/



    }
}
