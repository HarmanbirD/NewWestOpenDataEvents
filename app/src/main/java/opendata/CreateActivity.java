package opendata;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import ca.bcit.myapplication.R;
import ca.bcit.myapplication.login.LoginActivity;
import opendata.database.CulturalEvent;

public class CreateActivity
    extends AppCompatActivity
    implements View.OnClickListener
{

    private FirebaseAuth firebaseAuth;

    private TextInputLayout     textInputLayoutName;
    private TextInputLayout     textInputLayoutAddress;
    private TextInputLayout     textInputLayoutDescription;
    private TextInputLayout     textInputLayoutWebsite;

    private TextInputEditText   textInputEditTextName;
    private TextInputEditText   textInputEditTextAddress;
    private TextInputEditText   textInputEditTextDescription;
    private TextInputEditText   textInputEditTextWebsite;

    private AppCompatButton     appCompatButtonRegister;
    private AppCompatTextView   appCompatTextViewCancel;
    private ProgressDialog      progressDialog;
    private DatabaseReference   databaseReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        firebaseAuth = FirebaseAuth.getInstance();
        initViews();
        initListeners();
        initObjects();
    }

    private void initViews()
    {

        textInputLayoutName                 = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        textInputLayoutAddress              = (TextInputLayout) findViewById(R.id.textInputLayoutAddress);
        textInputLayoutDescription          = (TextInputLayout) findViewById(R.id.textInputLayoutDescription);
        textInputLayoutWebsite              = (TextInputLayout) findViewById(R.id.textInputLayoutWebsite);

        textInputEditTextName               = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        textInputEditTextAddress            = (TextInputEditText) findViewById(R.id.textInputEditTextAddress);
        textInputEditTextDescription        = (TextInputEditText) findViewById(R.id.textInputEditTextDescription);
        textInputEditTextWebsite            = (TextInputEditText) findViewById(R.id.textInputEditTextWebsite);

        appCompatButtonRegister             = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);

        appCompatTextViewCancel             = (AppCompatTextView) findViewById(R.id.appCompatTextViewCancel);

        databaseReference = FirebaseDatabase.getInstance().getReference("UserEvent");

    }

    private void initListeners()
    {
        appCompatButtonRegister.setOnClickListener(this);
        appCompatTextViewCancel.setOnClickListener(this);
    }

    private void initObjects()
    {
        progressDialog  = new ProgressDialog(this);
    }

    public void createEvent()
    {
        CulturalEvent culturalEvent = new CulturalEvent();

        String name         = textInputEditTextName.getText().toString();
        String address      = textInputEditTextAddress.getText().toString();
        String description  = textInputEditTextDescription.getText().toString();
        String website      = textInputEditTextWebsite.getText().toString();

        culturalEvent.setName(name);
        culturalEvent.setAddress(address);
        culturalEvent.setDescription(description);
        culturalEvent.setWebsite(website);

        String eyeD = databaseReference.push().getKey();
        databaseReference.child(eyeD).setValue(culturalEvent);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.appCompatButtonRegister:
                createEvent();
                finish();
                break;
            case R.id.appCompatTextViewCancel:
                finish();
                break;
        }
    }
}
