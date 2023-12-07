package com.tasks.taskmanager.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.State;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.material.snackbar.Snackbar;
import com.tasks.taskmanager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Settings extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String USERNAME_KEY = "username";

    public static final String TAG = "SittingActivity";

    Spinner teamSpinner = null;

    Spinner taskStateSpinner = null;

    CompletableFuture<List<Team>> teamFuture = new CompletableFuture<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button back = (Button) findViewById(R.id.backSitting);

        back.setOnClickListener(v -> {
            Intent goBack = new Intent(Settings.this, MainActivity.class);
            goBack.putExtra("selectedTeam", teamSpinner.getSelectedItem().toString()); // Pass selected team name
            startActivity(goBack);
        });

        setTeamSpinner();

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        Button saveButton = findViewById(R.id.saveButton);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        saveButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            prefs.edit().putString(USERNAME_KEY, username).apply();

            showSnackbar("Username saved: " + username);

        });
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    private void setTeamSpinner(){
        teamSpinner = (Spinner) findViewById(R.id.teamSpinnerSitting);

        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG, " Reading Teams success");
                    ArrayList<String> teamName = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    for (Team team : success.getData()){
                        teams.add(team);
                        teamName.add(team.getName());
                    }
                    teamFuture.complete(teams);

                    runOnUiThread(()-> {
                        teamSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                (android.R.layout.simple_spinner_item),
                                teamName
                        ));
                    });
                },
                failure -> {
                    teamFuture.complete(null);
                    Log.i(TAG, "Did not read any team");
                }
        );

        taskStateSpinner = (Spinner) findViewById(R.id.teamSpinnerSitting);

        taskStateSpinner.setAdapter(new ArrayAdapter<>(
                this,
                (android.R.layout.simple_spinner_item),
                State.values()
        ));
    }
}
