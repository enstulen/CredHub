package com.uc3m.credhub;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

public class DetailActivity extends AppCompatActivity {

    TextView descriptionTextView, usernameTextView, passwordTextView;
    Button showPasswordButton, exportButton;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);

        String description = getIntent().getStringExtra("description");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        setTitle(username);

        descriptionTextView = findViewById(R.id.detail_view_description_textView);
        usernameTextView = findViewById(R.id.detail_view_username_textView);
        passwordTextView = findViewById(R.id.detail_view_password_textView);

        descriptionTextView.setText(description);
        usernameTextView.setText(username);
        passwordTextView.setText("*******");

        showPasswordButton = findViewById(R.id.show_password_button);
        exportButton = findViewById(R.id.export_button);


        View.OnClickListener clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.show_password_button:
                        showPassword();
                        break;

                    case R.id.export_button:
                        exportRecord();
                        break;

                    default:
                        break;
                }
            }
        };

        showPasswordButton.setOnClickListener(clickListener);
        exportButton.setOnClickListener(clickListener);

    }

    public void showPassword() {
        passwordTextView.setText(password);
    }

    public void exportRecord() {
        String uuid = UUID.randomUUID().toString();
        SOAPSingleton soapSingleton = SOAPSingleton.getInstance();
        soapSingleton.exportRecord(uuid, username, password);

    }
}
