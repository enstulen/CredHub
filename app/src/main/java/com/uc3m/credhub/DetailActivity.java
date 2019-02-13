package com.uc3m.credhub;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);

        String name= getIntent().getStringExtra("name");
        setTitle(name);


        TextView textView = findViewById(R.id.detail_textView);
        textView.setText(name);
    }
    }
