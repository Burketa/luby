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

    private final String TEST_EMAIL = "teste@luby.com.br";
    private final String TEST_PASSWORD = "123456";

    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;
    private ImageView imageLock;
    private TextView textToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //É possível otimizar esse bloco
        //{
        User defaultUser = new User();
        defaultUser.setEmail(TEST_EMAIL);
        defaultUser.setPassword(TEST_PASSWORD);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(TEST_EMAIL, new Gson().toJson(defaultUser));
        editor.commit();
        //}

        assignUIElementsReferences();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_UP_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.user_registered, Toast.LENGTH_SHORT).show();
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

    //Open the SignIn activity
    public void openSignInActivity(View view) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String userJson = sharedPref.getString(inputEmail.getEditText().getText().toString(), "");
        System.out.println("userJSON -> " + userJson);

        if (userJson.equals("")) {
            Toast.makeText(this, R.string.user_invalid, Toast.LENGTH_SHORT).show();
        } else {
            Gson gson = new Gson();
            User user = gson.fromJson(userJson, User.class);

            if (inputPassword.getEditText().getText().toString().equals(user.getPassword())) {
                getToken(user);
            } else {
                Toast.makeText(this, R.string.password_invalid + user.getPassword(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Open the registration activity
    public void openSignUpActivity(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivityForResult(intent, SIGN_UP_CODE);
    }

    //Change the lock image
    private void changeLockImage() {
        imageLock.setImageResource(R.drawable.lock_unlocked);
        imageLock.setContentDescription(getResources().getString(R.string.image_description_lock_unlocked));
    }

    //Generate a new token
    private void getToken(final User user) {
        LoginService service = new LoginService(this, new Callable<Void>() {
            @Override
            public Void call() {
                showToken(user);

                return null;
            }
        });
        service.getToken(user, this);
    }

    //Get the stored token in shared preferences and show it on edittext
    public void showToken(User user)
    {
        changeLockImage();
        Toast.makeText(this, R.string.welcome + user.getName(), Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String tokenJSON = sharedPref.getString(Token.TOKEN_TAG, "");

        Token token = new Gson().fromJson(tokenJSON, Token.class);
        textToken.setText(token.toString());
        textToken.setVisibility(View.VISIBLE);
    }

}
