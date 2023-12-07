package com.tasks.taskmanager.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.tasks.taskmanager.R;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "SignUpActivity";

    public static final String SIGNUP_EMAIL_TAG = "SignUp_Email_Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button signUpSubmitButton = (Button) findViewById(R.id.SignUpSubmitButton);
        signUpSubmitButton.setOnClickListener(v ->
        {
            String username = ((EditText)findViewById(R.id.SignUpUsernameEdit)).getText().toString();
            String password = ((EditText)findViewById(R.id.SignUpPasswordEdit)).getText().toString();

            Amplify.Auth.signUp(
                    username,
                password,
                AuthSignUpOptions
                        .builder()
                        .userAttribute(AuthUserAttributeKey.email(), "ghaidaarowad@gmail.com")
                        .userAttribute(AuthUserAttributeKey.nickname(), "Gk")
                        .build(),
                good -> {
            Log.i(TAG, "Success signup: " + good.toString());
                    Intent goToVerifyIntent = new Intent(SignUpActivity.this, VerifyAccountActivity.class);
                    goToVerifyIntent.putExtra(SIGNUP_EMAIL_TAG, username);
                    startActivity(goToVerifyIntent);
                },
                bad -> {
            Log.i(TAG, "Failed signup with username: " + username + "with this message : " + bad.toString());
            runOnUiThread(() ->{
                Toast.makeText(SignUpActivity.this, "Sign Up failed", Toast.LENGTH_LONG);
            });
                }
            );
        });
    }
}