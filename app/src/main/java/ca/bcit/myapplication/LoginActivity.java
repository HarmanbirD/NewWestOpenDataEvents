package ca.bcit.myapplication;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;
import ca.bcit.myapplication.helpers.ValidationOfInput;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private final AppCompatActivity activity = LoginActivity.this;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;

    private AppCompatTextView textViewLinkRegister;

    private ValidationOfInput inputValidation;
    private DataBaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initListeners();
        initObjects();
    }

    private void initViews()
    {
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputTextEmailEdit);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputTextPasswordEdit);

        appCompatButtonLogin = (AppCompatButton) findViewById(R.id.appCompatButtonLogin);

        textViewLinkRegister = (AppCompatTextView) findViewById(R.id.textViewLinkRegister);
    }

    private void initListeners()
    {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
    }

    private void initObjects()
    {
        databaseHelper = new DataBaseHelper(activity);
        inputValidation = new ValidationOfInput(activity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.appCompatButtonLogin:
                verifyFromSQLite();
                break;
            case R.id.textViewLinkRegister:
                finish();
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void verifyFromSQLite()
    {
        if (!inputValidation.inputTextFieldEditEmpty(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_empty_email)))
        {
            return;
        }
        if (!inputValidation.inputTextEmailEditValid(textInputEditTextEmail, textInputLayoutEmail, getString(R.string.error_invalid_email)))
        {
            return;
        }
        if (!inputValidation.inputTextFieldEditEmpty(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_empty_password)))
        {
            return;
        }

        if (databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim(),
                textInputEditTextPassword.getText().toString().trim()))
        {
            finish();
            Intent intent = new Intent(activity, MainActivity.class);
            emptyInputEditText();
            startActivity(intent);

        } else {
            Toast.makeText(LoginActivity.this, getString(R.string.error_invalid_email_password), Toast.LENGTH_LONG).show();
        }
    }

    private void emptyInputEditText()
    {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }

}

