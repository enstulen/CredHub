package com.uc3m.credhub;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {

    EditText editDescription, editUsername, editPassword;
    Button saveButton;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_view);
        setTitle("Add new");

        db = new DatabaseHelper(this);

        editDescription = (EditText) findViewById(R.id.text_field_description);
        editUsername = (EditText) findViewById(R.id.text_field_username);
        editPassword = (EditText) findViewById(R.id.text_field_password);

        saveButton = (Button) findViewById(R.id.save_button);
        addData();

    }

    public void addData(){
        saveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isinserted = db.insertData(editDescription.getText().toString(), editUsername.getText().toString(), editPassword.getText().toString());
                        if (isinserted == true) {
                            finish();
                        } else
                            Toast.makeText(AddActivity.this, "Data not inserted", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
