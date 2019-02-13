package com.simonk.project.prettyrss.ui.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.simonk.project.prettyrss.R;
import com.simonk.project.prettyrss.databinding.WebActivityBinding;
import com.simonk.project.prettyrss.ui.BindingActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.SharedElementCallback;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public class WebActivity extends BindingActivity {

    private static final String URL_EXTRA = "com.simonk.project.prettyrss.ui.web.WebActivity.URL_EXTRA";

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
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);

        ProgressBar progressBar = getBinding().webActivityProgress;
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if(progress < 100){
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                progressBar.setProgress(progress);
                if(progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }

        });

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
