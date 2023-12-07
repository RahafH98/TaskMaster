package com.tasks.taskmanager.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import com.tasks.taskmanager.R;

public class VerifyAccountActivity extends AppCompatActivity {

    public static final String TAG = "VerifyAccountActivity";

    public static final String VERIFY_ACC_EMAIL_TAG = "Verify_Acc_Email_Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        Intent callingIntent = getIntent();
        String email = callingIntent.getStringExtra(SignUpActivity.SIGNUP_EMAIL_TAG);
        EditText usernameEditText = (EditText) findViewById(R.id.VertifyUsernameEdit);
        usernameEditText.setText(email);

        Button verifyAccButton = findViewById(R.id.VerifySubmitButton);
        verifyAccButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String verificationCode = ((EditText)findViewById(R.id.VerificationCodeEdit)).getText().toString();

                Amplify.Auth.confirmSignUp(username,
                verificationCode,
                good -> {
                    Log.i(TAG, "verification succeeded: " + good.toString());
                    Intent goToLogInIntent = new Intent(VerifyAccountActivity.this, LogInActivity.class);
                    goToLogInIntent.putExtra(VERIFY_ACC_EMAIL_TAG, username);
                    startActivity(goToLogInIntent);
                },

                fail -> {
                    Log.i(TAG, "verification failed: " + fail.toString());
                    runOnUiThread(() -> {
                        Toast.makeText(VerifyAccountActivity.this, "Verify acc failed :( !!", Toast.LENGTH_LONG);
                    });
        });
        });
    }
}