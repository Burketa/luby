package com.burca.lubyapp.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.burca.lubyapp.R;
import com.burca.lubyapp.model.User;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class RegistrationActivity extends AppCompatActivity {

    private final Calendar calendar = Calendar.getInstance();

    private ArrayList<AwesomeValidation> validations = new ArrayList<>();

    private TextInputLayout inputName;
    private TextInputLayout inputPhone;
    private TextInputLayout inputBirthday;
    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;

    private AwesomeValidation nameValidation = new AwesomeValidation(BASIC);
    private AwesomeValidation phoneValidation = new AwesomeValidation(BASIC);
    private AwesomeValidation birthdayValidation = new AwesomeValidation(BASIC);
    private AwesomeValidation emailValidation = new AwesomeValidation(BASIC);
    private AwesomeValidation passwordValidation = new AwesomeValidation(BASIC);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        assignUIElementsReferences();
    }

    private void assignUIElementsReferences() {
        validations.add(nameValidation);
        validations.add(phoneValidation);
        validations.add(birthdayValidation);
        validations.add(emailValidation);

        Button buttonSignIn = findViewById(R.id.button_sign_in);

        inputName = findViewById(R.id.input_name);
        inputPhone = findViewById(R.id.input_phone);
        inputEmail = findViewById(R.id.input_email);
        inputBirthday = findViewById(R.id.input_birthday);
        inputPassword = findViewById(R.id.input_password);

        nameValidation.addValidation(this, R.id.input_name, "[a-zA-Z\\s]+", R.string.invalid_input);
        phoneValidation.addValidation(this, R.id.input_phone, RegexTemplate.NOT_EMPTY, R.string.invalid_input);
        birthdayValidation.addValidation(this, R.id.input_birthday, "^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$", R.string.invalid_input);
        emailValidation.addValidation(this, R.id.input_email, android.util.Patterns.EMAIL_ADDRESS, R.string.invalid_input);
        passwordValidation.addValidation(this, R.id.input_password, RegexTemplate.NOT_EMPTY, R.string.invalid_input);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //System.out.println("D -> " + calendar.get(Calendar.DAY_OF_MONTH));
                //System.out.println("M -> " + calendar.get(Calendar.MONTH));
                //System.out.println("Y -> " + calendar.get(Calendar.YEAR));
                updateBirthdayText();
            }

        };

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = false;

                for (AwesomeValidation validation : validations) {
                    valid = validation.validate();
                    if (!valid)
                        break;
                }
                if (valid)
                    signInClick(v);
            }
        });
        inputBirthday.getEditText().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegistrationActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void signInClick(View view) {
        if (validateInputFields())
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
        newUser.birthday = inputBirthday.getEditText().getText().toString();

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

    private void updateBirthdayText() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        inputBirthday.getEditText().setText(sdf.format(calendar.getTime()));
    }
}
