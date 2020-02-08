package com.burca.lubyapp.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.burca.lubyapp.R;
import com.burca.lubyapp.model.User;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputLayout inputName;
    private TextInputLayout inputPhone;
    private TextInputLayout inputBirthday;
    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ActionBar ab = getSupportActionBar();
        if(ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        assignUIElementsReferences();
    }

    private void assignUIElementsReferences() {
        Button buttonSignIn = findViewById(R.id.button_sign_in);

        inputName = findViewById(R.id.input_name);
        inputPhone = findViewById(R.id.input_phone);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInClick(v);
            }
        });
    }

    public void signInClick(View view) {
        if(validateInputFields())
            persistUser(createUser());
    }

    private boolean validateInputFields() {
        return true;
    }

    public User createUser() {
        User newUser = new User();

        newUser.name = inputName.getEditText().getText().toString();
        newUser.phone = inputPhone.getEditText().getText().toString();
        newUser.email = inputEmail.getEditText().getText().toString();
        newUser.password = inputPassword.getEditText().getText().toString();
        //newUser.birthday = inputBirthday.getEditText().getText().toString();

        return newUser;
    }

    private void persistUser(User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        //System.out.println("JSON -> " + userJson);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(user.email, userJson);
        editor.commit();

        Intent intent = new Intent();
        intent.putExtra(MainActivity.EMAIL_KEY, user.email);
        setResult(RESULT_OK, intent);
        finish();
    }
}
