package ca.bcit.myapplication.login;

import android.app.ProgressDialog;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import ca.bcit.myapplication.R;
import ca.bcit.myapplication.helpers.ValidationOfInput;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private final AppCompatActivity activity = LoginActivity.this;

    private TextInputLayout     textInputLayoutEmail;
    private TextInputLayout     textInputLayoutPassword;

    private TextInputEditText   textInputEditTextEmail;
    private TextInputEditText   textInputEditTextPassword;

    private AppCompatButton     appCompatButtonLogin;

    private AppCompatTextView   textViewLinkRegister;

    private ValidationOfInput   inputValidation;
    private DataBaseHelper      databaseHelper;

    private ProgressDialog      progressDialog;
    private FirebaseAuth        firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null)
        {
            loggedIn();
        }

        initViews();
        initListeners();
        initObjects();
    }

    private void initViews()
    {
        textInputLayoutEmail        = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword     = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        textInputEditTextEmail      = (TextInputEditText) findViewById(R.id.textInputTextEmailEdit);
        textInputEditTextPassword   = (TextInputEditText) findViewById(R.id.textInputTextPasswordEdit);

        appCompatButtonLogin        = (AppCompatButton) findViewById(R.id.appCompatButtonLogin);

        textViewLinkRegister        = (AppCompatTextView) findViewById(R.id.textViewLinkRegister);
    }

    private void initListeners()
    {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
    }

    private void initObjects()
    {
        progressDialog  = new ProgressDialog(this);
        databaseHelper  = new DataBaseHelper(activity);
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
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void verifyFromSQLite()
    {
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

        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(textInputEditTextEmail.getText().toString(),
                                                textInputEditTextPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            loggedIn();
                            emptyInputEditText();
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, getString(R.string.error_invalid_email_password), Toast.LENGTH_LONG).show();
                        }
                    }
                });

//        if (databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim(),
//                textInputEditTextPassword.getText().toString().trim()))
//        {
//            finish();
//            Intent intent = new Intent(activity, opendata.MainActivity.class);
//            emptyInputEditText();
//            startActivity(intent);
//
//        } else {
//            Toast.makeText(LoginActivity.this, getString(R.string.error_invalid_email_password), Toast.LENGTH_LONG).show();
//        }
    }

    private void loggedIn()
    {
        finish();
        Intent intent = new Intent(activity, opendata.MainActivity.class);
        startActivity(intent);
    }

    private void emptyInputEditText()
    {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }

}

