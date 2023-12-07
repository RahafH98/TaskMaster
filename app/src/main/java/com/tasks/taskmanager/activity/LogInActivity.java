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

public class LogInActivity extends AppCompatActivity {

    public static final String TAG = "LogInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Intent callignIntent = getIntent();
        String email = callignIntent.getStringExtra(VerifyAccountActivity.VERIFY_ACC_EMAIL_TAG);
        EditText usernameEditText = (EditText) findViewById(R.id.LogInUsernameEdit);
        usernameEditText.setText(email);

        Button logInButton = (Button) findViewById(R.id.LogInFromLogInButton);
        logInButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = ((EditText)findViewById(R.id.LogInPasswordText)).getText().toString();

            Amplify.Auth.signIn(username,
                password,
                success -> {
                    Log.i(TAG, "Log in succeeded: " + success.toString());
                    Intent goToMain = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(goToMain);
                },
                fail -> {
                    Log.i(TAG, "did not log in: " + fail.toString());
                    runOnUiThread(() -> {
                        Toast.makeText(LogInActivity.this, "Log in failed :( !!" , Toast.LENGTH_LONG);
                    });
                }
                );
        });

        Button sginUpButton = (Button) findViewById(R.id.SignUpFromLogIn);
        sginUpButton.setOnClickListener(v -> {
            Intent goToSignUpIntent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivity(goToSignUpIntent);
        });
    }
}