package com.codepath.apps.restclienttemplate;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComposeActivity extends AppCompatActivity {

    @BindView(R.id.tvTweet) TextView tvTweet;
    @BindView(R.id.btnTweet) Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);
    }

//    public void onClick(View v)
//    {
//        Toast.makeText(ComposeActivity.this, "Success", Toast.LENGTH_SHORT).show();
//    }
}
