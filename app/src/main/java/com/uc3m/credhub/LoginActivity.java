package com.uc3m.credhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;
    Button loginButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        editTextUsername = (EditText) findViewById(R.id.login_username_editText);
        editTextPassword = (EditText) findViewById(R.id.login_password_editText);

        loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
                        SharedPreferences.Editor mEditor = prefs.edit();
                        mEditor.putString("username", editTextUsername.getText().toString());
                        mEditor.putString("password", editTextPassword.getText().toString());
                        mEditor.commit();


                        MessageDigest md = null;
                        try {
                            md = MessageDigest.getInstance("SHA-256");
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        String password = editTextPassword.getText().toString();
                        String passwordWithSalt = getString(R.string.salt) + password;

                        md.update(passwordWithSalt.getBytes(StandardCharsets.UTF_8));
                        byte[] digest = md.digest();

                        String hex = String.format("%064x", new BigInteger(1, digest));
                        System.out.println(hex);

                        if (hex.equals(getString(R.string.hash))) {
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                        } else {
                            Toast.makeText(LoginActivity.this,"Wrong username or password", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );






    }


}
