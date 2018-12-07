package com.simonk.project.ppoproject.ui.web;

import android.app.AppComponentFactory;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.databinding.ActivityMainBinding;
import com.simonk.project.ppoproject.databinding.WebActivityBinding;
import com.simonk.project.ppoproject.ui.BindingActivity;

import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.SharedElementCallback;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public class WebActivity extends BindingActivity {

    private static final String URL_EXTRA = "com.simonk.project.ppoproject.ui.web.WebActivity.URL_EXTRA";

    public static Intent getIntent(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(URL_EXTRA, url);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = getIntent().getStringExtra(URL_EXTRA);

        WebView webView = getBinding().webActivityWebView;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);

        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.shared_element_transition);
        getWindow().setSharedElementEnterTransition(transition);
    }

    @Override
    protected ViewDataBinding initBinding() {
        return DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.web_activity, null, false);
    }

    @Override
    public WebActivityBinding getBinding() {
        return (WebActivityBinding) super.getBinding();
    }

}
