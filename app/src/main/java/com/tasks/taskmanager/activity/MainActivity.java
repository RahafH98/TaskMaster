package com.tasks.taskmanager.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.tasks.taskmanager.R;
import com.tasks.taskmanager.activity.adapter.TasksListRecyclerViewAdapter;
import com.amplifyframework.datastore.generated.model.State;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String DATABASE_NAME = "taskDb";

    public static final String TAG = "MainActivity";

    List<Task> tasks = new ArrayList<>();

    TasksListRecyclerViewAdapter adapter;

    public static final String TASK_ID_TAG = "Task ID Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpTasksRecyclerView();

        
//        Intent intent = getIntent();
//        String selectedTeam = intent.getStringExtra("selectedTeam");

        Button addTask = findViewById(R.id.addTaskButton);

        addTask.setOnClickListener(v -> {
            Intent goToAddTaskPage = new Intent(MainActivity.this, AddTask.class);
            startActivity(goToAddTaskPage);
        });

        Button allTasks = findViewById(R.id.allTasksButton);

        allTasks.setOnClickListener(v -> {
            Intent goToAllTasksPage = new Intent(MainActivity.this, AllTasks.class);
            startActivity(goToAllTasksPage);
        });

        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            Intent goToSettings = new Intent(MainActivity.this, Settings.class);
            startActivityForResult(goToSettings, 1);
        });

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");

        TextView usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(username + "'s tasks");

        Amplify.API.query(
                ModelQuery.list(Task.class),
                success ->{
                    Log.i("anything", "Read Task successfully");
                    tasks.clear();
                    for (Task databaseTask: success.getData()){
                        tasks.add(databaseTask);
                    }
                    runOnUiThread(() ->{
                        adapter.notifyDataSetChanged();
                    });
                },
                failure -> Log.i("anything", "Did not red Task")
        );
//        Team team1 = Team.builder()
//                         .name("Team 1")
//                         .build();
//
//        Team team2 = Team.builder()
//                .name("Team 2")
//                .build();
//
//        Team team3 = Team.builder()
//                .name("Team 3")
//                .build();
//
//        Team team4 = Team.builder()
//                .name("Team 4")
//                .build();
//
//        Amplify.API.mutate(
//                ModelMutation.create(team1),
//                successRes -> Log.i(TAG, "MainActivity, made a team successfully"),
//                failureRes -> Log.i(TAG, "MainActivity, failed making a team")
//        );
//
//        Amplify.API.mutate(
//                ModelMutation.create(team2),
//                successRes -> Log.i(TAG, "MainActivity, made a team successfully"),
//                failureRes -> Log.i(TAG, "MainActivity, failed making a team")
//        );
//
//        Amplify.API.mutate(
//                ModelMutation.create(team3),
//                successRes -> Log.i(TAG, "MainActivity, made a team successfully"),
//                failureRes -> Log.i(TAG, "MainActivity, failed making a team")
//        );
//
//        Amplify.API.mutate(
//                ModelMutation.create(team4),
//                successRes -> Log.i(TAG, "MainActivity, made a team successfully"),
//                failureRes -> Log.i(TAG, "MainActivity, failed making a team")
//        );

//        Amplify.API.query(
//                ModelQuery.list(Task.class),
//                success ->{
//                    Log.i(TAG, "Read Task successfully");
//                    tasks.clear();
//                    for (Task databaseTask : success.getData()){
//                        if (databaseTask.getTeamTask().getName().equals(selectedTeam)) {
//                            tasks.add(databaseTask);
//                        }
//                    }
//                    runOnUiThread(() ->{
//                        adapter.notifyDataSetChanged();
//                    });
//                },
//                failure -> Log.i(TAG, "Did not red Task")
//        );

//        String emptyFileName = "EmptyTetFileName";
//        File emptyFile = new File(getApplicationContext().getFilesDir(), emptyFileName);
//
//        try {
//            BufferedWriter emptyFileBufferedWriter = new BufferedWriter(new FileWriter(emptyFileName));
//
//            emptyFileBufferedWriter.append("Some text from me gg \n another text from gg2");
//
//            emptyFileBufferedWriter.close();
//        }catch (IOException ioe){
//            Log.i(TAG, "Could not write locally with filename" + emptyFileName);
//        }
//
//        String emptyFileS3Key = "someFileInS3.txt";
//
//        Amplify.Storage.uploadFile(
//                emptyFileS3Key,
//                emptyFile,
//                success -> {
//                    Log.i(TAG, "S3 upload success and the key is : " + success.getKey());
//                },
//                fail -> {
//                    Log.i(TAG, "S3 upload failed!! " + fail.getMessage(), fail);
//                }
//        );

        init();
        setUpLogInAndLogOutButtons();
    }

    private void init(){
        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("openedApp")
                .addProperty("time", Long.toString(new Date().getTime()))
                .addProperty("trackingEvent", "main activity opened")
                .build();

        Amplify.Analytics.recordEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String username = data.getStringExtra("username");

            TextView usernameTextView = findViewById(R.id.usernameTextView);
            usernameTextView.setText(username + "'s tasks");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
//        String username = prefs.getString("username", "");
//
//        TextView usernameTextView = findViewById(R.id.usernameTextView);
//        usernameTextView.setText(username + "'s tasks");

        AuthUser authUser = Amplify.Auth.getCurrentUser();

        String username = "";

        if (authUser == null){
            Button loginButton = (Button) findViewById(R.id.MainLogInButton);
            loginButton.setVisibility(View.VISIBLE);
            Button logoutButton = (Button) findViewById(R.id.MainLogOutButton);
            logoutButton.setVisibility(View.INVISIBLE);
        }else {
            username = authUser.getUsername();
            Log.i(TAG, "User name is: " + username);
            Button loginButton = (Button) findViewById(R.id.MainLogInButton);
            loginButton.setVisibility(View.INVISIBLE);
            Button logoutButton = (Button) findViewById(R.id.MainLogOutButton);
            logoutButton.setVisibility(View.VISIBLE);

            String username2 = username;
            Amplify.Auth.fetchUserAttributes(
                    success -> {
                        Log.i(TAG, "Fetching username Attribute: " + username2);
                        for (AuthUserAttribute userAttribute : success){
                            if (userAttribute.getKey().getKeyString().equals("email")){
                                String userEmail = userAttribute.getValue();
                                runOnUiThread(()->{
                                    ((TextView)findViewById(R.id.usernameTextView)).setText(userEmail);
                                });
                            }
                        }
                    },
                    fail -> {
                        Log.i(TAG, "Failed Fetching username Attribute");
                    }
            );
        }

        readTasks();
    }

    private void setUpTasksRecyclerView() {
        RecyclerView tasksListRecyclerView = findViewById(R.id.tasksListRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        tasksListRecyclerView.setLayoutManager(layoutManager);

        adapter = new TasksListRecyclerViewAdapter(tasks, this);
        tasksListRecyclerView.setAdapter(adapter);
    }

    private void readTasks() {
        Intent intent = getIntent();
        String selectedTeam = intent.getStringExtra("selectedTeam");

        Amplify.API.query(
                ModelQuery.list(Task.class),
                success -> {
                    Log.i(TAG, "Read Task successfully");
                    Log.i(TAG, "success.getData()"+success.getData().toString());
                    tasks.clear();
                    for (Task databaseTask : success.getData()) {
                        if (databaseTask.getTeamTask().getName().equals(selectedTeam)) {
                            tasks.add(databaseTask);
                        }
                    }
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                    });
                },
                failure -> Log.i(TAG, "Did not read Task")
        );
    }

    private void setUpLogInAndLogOutButtons(){
        Button loginButton = (Button) findViewById(R.id.MainLogInButton);
        loginButton.setOnClickListener(v -> {
            Intent goToLogInIntent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(goToLogInIntent);
        });

        Button logoutButton = (Button) findViewById(R.id.MainLogOutButton);
        logoutButton.setOnClickListener(v -> {
            Amplify.Auth.signOut(
                ()->{
                    Log.i(TAG, "Logout success");
                    runOnUiThread(() -> {
                        ((TextView)findViewById(R.id.usernameTextView)).setText("");
                        Intent goToLogInIntent = new Intent(MainActivity.this, LogInActivity.class);
                        startActivity(goToLogInIntent);
                    });
                },
                fail -> {
                    Log.i(TAG, "Logout failed");
                    runOnUiThread(()->{
                        Toast.makeText(MainActivity.this, "Log out failed :( !!", Toast.LENGTH_LONG);
                    });
                }
        );
        });
    }
}
