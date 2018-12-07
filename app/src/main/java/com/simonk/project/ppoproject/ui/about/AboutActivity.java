package com.simonk.project.ppoproject.ui.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.simonk.project.ppoproject.R;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
    }

}
