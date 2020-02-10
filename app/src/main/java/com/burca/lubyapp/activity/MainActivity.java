package com.burca.lubyapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.burca.lubyapp.R;
import com.burca.lubyapp.model.Token;
import com.burca.lubyapp.model.User;
import com.burca.lubyapp.service.LoginService;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {

    public static final int SIGN_UP_CODE = 1;
    public static final String EMAIL_KEY = "email";

    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;
    private ImageView imageLock;
    private TextView textToken;

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
        imageLock = findViewById(R.id.image_lock);
        inputEmail = findViewById(R.id.user_email);
        inputPassword = findViewById(R.id.user_password);
        textToken = findViewById(R.id.text_token);
    }

    public void openSignInActivity(View view) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String userJson = sharedPref.getString(inputEmail.getEditText().getText().toString(), "");

        if (userJson.equals("")) {
            Toast.makeText(this, "Usuario inválido", Toast.LENGTH_SHORT).show();
        } else {
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);

            if (inputPassword.getEditText().getText().toString().equals(user.password)) {
                Toast.makeText(this, "Bem vindx\n" + user.name, Toast.LENGTH_SHORT).show();

                changeLockImage();

                getToken(user);
            } else {
                Toast.makeText(this, "Senha inválida: " + user.password, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openSignUpActivity(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivityForResult(intent, SIGN_UP_CODE);
    }

    private void changeLockImage() {
        imageLock.setImageResource(R.drawable.lock_unlocked);
        imageLock.setContentDescription(getResources().getString(R.string.image_description_lock_unlocked));
    }

    //Generate a new token
    private void getToken(User user) {
        System.out.println("User ->" + user.toString());

        LoginService service = new LoginService(this, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                showToken();
                return null;
            }
        });
        service.getToken(user, this);
    }

    //Get the stored token in shared preferences and show it on edittext
    public void showToken()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String tokenJSON = sharedPref.getString("token", "");

        Token token = new Gson().fromJson(tokenJSON, Token.class);
        textToken.setText(token.toString());
        textToken.setVisibility(View.VISIBLE);
    }

}
