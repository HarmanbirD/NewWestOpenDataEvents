package ca.bcit.myapplication.helpers;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;

public class ValidationOfInput
{
    private Context context;

    public ValidationOfInput(Context context)
    {
        this.context = context;
    }

    public boolean inputTextFieldEditEmpty(TextInputEditText textInputEditText,
                                           TextInputLayout textInputLayout,
                                           String message)
    {
        String input = textInputEditText.getText().toString().trim();
        if (input.isEmpty()) {
            textInputLayout.setError(message);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    public boolean inputTextEmailEditValid(TextInputEditText textInputEditText,
                                           TextInputLayout textInputLayout,
                                           String message)
    {
        String input = textInputEditText.getText().toString().trim();
        if (input.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches())
        {
            textInputLayout.setError(message);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean inputEditTextMatches(TextInputEditText textInputEditText1,
                                        TextInputEditText textInputEditText2,
                                        TextInputLayout textInputLayout,
                                        String message)
    {
        String value1 = textInputEditText1.getText().toString().trim();
        String value2 = textInputEditText2.getText().toString().trim();
        if (!value1.contentEquals(value2)) {
            textInputLayout.setError(message);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }
}
