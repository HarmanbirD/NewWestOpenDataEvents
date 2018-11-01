package ca.bcit.myapplication.login;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import ca.bcit.myapplication.R;
import ca.bcit.myapplication.helpers.ValidationOfInput;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener
{

    private final AppCompatActivity activity = RegisterActivity.this;

    private TextInputLayout     textInputLayoutName;
    private TextInputLayout     textInputLayoutEmail;
    private TextInputLayout     textInputLayoutPassword;
    private TextInputLayout     textInputLayoutConfirmPassword;

    private TextInputEditText   textInputEditTextName;
    private TextInputEditText   textInputEditTextEmail;
    private TextInputEditText   textInputEditTextPassword;
    private TextInputEditText   textInputEditTextConfirmPassword;

    private AppCompatButton     appCompatButtonRegister;
    private AppCompatTextView   appCompatTextViewLoginLink;

    private ValidationOfInput   inputValidation;
    private DataBaseHelper      databaseHelper;
    private User                user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initListeners();
        initObjects();
    }

    /**
     * This method is to initialize views
     */
    private void initViews()
    {

        textInputLayoutName                 = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        textInputLayoutEmail                = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword             = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword      = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);

        textInputEditTextName               = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        textInputEditTextEmail              = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword           = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);
        textInputEditTextConfirmPassword    = (TextInputEditText) findViewById(R.id.textInputEditTextConfirmPassword);

        appCompatButtonRegister             = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);

        appCompatTextViewLoginLink          = (AppCompatTextView) findViewById(R.id.appCompatTextViewLoginLink);

    }

    private void initListeners()
    {
        appCompatButtonRegister.setOnClickListener(this);
        appCompatTextViewLoginLink.setOnClickListener(this);
    }

    private void initObjects()
    {
        inputValidation = new ValidationOfInput(activity);
        databaseHelper = new DataBaseHelper(activity);
        user = new User();
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.appCompatButtonRegister:
                postDataToSQLite();
                break;
            case R.id.appCompatTextViewLoginLink:
                finish();
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void postDataToSQLite()
    {
        if (!inputValidation.inputTextFieldEditEmpty(textInputEditTextName,
                textInputLayoutName,
                getString(R.string.error_empty_name)))
        {
            return;
        }
        if (!inputValidation.inputTextFieldEditEmpty(textInputEditTextEmail,
                textInputLayoutEmail,
                getString(R.string.error_empty_email)))
        {
            return;
        }
        if (!inputValidation.inputTextEmailEditValid(textInputEditTextEmail,
                textInputLayoutEmail,
                getString(R.string.error_invalid_email)))
        {
            return;
        }
        if (!inputValidation.inputTextFieldEditEmpty(textInputEditTextPassword,
                textInputLayoutPassword,
                getString(R.string.error_empty_password)))
        {
            return;
        }
        if (!inputValidation.inputTextPasswordEditValid(textInputEditTextPassword,
                textInputLayoutPassword,
                getString(R.string.error_invalid_password)))
        {
            return;
        }
        if (!inputValidation.inputEditTextMatches(textInputEditTextPassword,
                textInputEditTextConfirmPassword,
                textInputLayoutConfirmPassword,
                getString(R.string.error_passwords_dont_match)))
        {
            return;
        }

        if (!databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim()))
        {

            user.setName(textInputEditTextName.getText().toString().trim());
            user.setEmail(textInputEditTextEmail.getText().toString().trim());
            user.setPassword(textInputEditTextPassword.getText().toString().trim());

            databaseHelper.addUser(user);

            Toast.makeText(RegisterActivity.this, getString(R.string.success_message), Toast.LENGTH_SHORT).show();
            emptyInputEditText();
            finish();
            Intent intent = new Intent(activity, opendata.MainActivity.class);
            startActivity(intent);

        } else {
            Toast.makeText(RegisterActivity.this, getString(R.string.error_email_exists), Toast.LENGTH_SHORT).show();
        }
    }

    private void emptyInputEditText()
    {
        textInputEditTextName.setText(null);
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
        textInputEditTextConfirmPassword.setText(null);
    }
}

