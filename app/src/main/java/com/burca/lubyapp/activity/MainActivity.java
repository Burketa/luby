package com.burca.lubyapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.burca.lubyapp.R;
import com.burca.lubyapp.model.User;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    public static final int SIGN_UP_CODE = 1;
    public static final String EMAIL_KEY = "email";

    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignUIElementsReferences();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_UP_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Usuario cadastrado", Toast.LENGTH_SHORT).show();
                inputEmail.getEditText().setText(data.getExtras().getString(EMAIL_KEY, ""));
            }
        }
    }

    private void assignUIElementsReferences() {
        inputEmail = findViewById(R.id.user_email);
        inputPassword = findViewById(R.id.user_password);
    }

    public void openSignInActivity(View view) {
        System.out.println("click");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String userJson = sharedPref.getString(inputEmail.getEditText().getText().toString(), "");

        if (userJson.equals("")) {
            Toast.makeText(this, "Usuario inválido", Toast.LENGTH_SHORT).show();
        } else {
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);


            if (inputPassword.getEditText().getText().toString().equals(user.password))
                Toast.makeText(this, "Usuario Logado", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this, "senha: " + user.password, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openSignUpActivity(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivityForResult(intent, SIGN_UP_CODE);
    }

}
