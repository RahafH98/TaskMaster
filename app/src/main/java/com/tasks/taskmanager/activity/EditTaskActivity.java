package com.tasks.taskmanager.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.State;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.material.snackbar.Snackbar;
import com.tasks.taskmanager.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EditTaskActivity extends AppCompatActivity {

    public static final String TAG = "EditTaskActivity";

    private CompletableFuture<Task> taskFuture = null;
    CompletableFuture<List<Team>> teamFuture = null;

    private Task tasksToEdit = null;

    private EditText titleEditText;

    private EditText descriptionEditText;

    private Spinner taskStateSpinner = null;

    private Spinner taskTeamSpinner = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        taskFuture = new CompletableFuture<>();
        teamFuture = new CompletableFuture<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpEditTask();
        setUpSave();
        setupDelete();
    }

    private void setUpEditTask(){
        Intent callingIntent = getIntent();
        String taskId = null;

        if (callingIntent != null){
            taskId = callingIntent.getStringExtra(MainActivity.TASK_ID_TAG);
        }

        String taskId2 = taskId;

        Amplify.API.query(
                ModelQuery.list(Task.class),
                success -> {
                    Log.i(TAG, "Read Tasks successfully");

                    for (Task databaseTask : success.getData()){
                        if (databaseTask.getId().equals(taskId2)){
                            taskFuture.complete(databaseTask);
                        }
                    }

                    runOnUiThread(() ->{});
                },
                failure -> Log.i(TAG, "Did not read task")
        );

        try {
            tasksToEdit = taskFuture.get();
        }catch (InterruptedException ie){
            Log.e(TAG, "InterruptedException while getting tasks");
            Thread.currentThread().interrupt();
        }catch (ExecutionException ee){
            Log.e(TAG, "ExecutionException while getting tasks");
        }

        titleEditText = findViewById(R.id.editTaskTitle);
        titleEditText.setText(tasksToEdit.getTitle());

        descriptionEditText = findViewById(R.id.editTaskDescription);
        descriptionEditText.setText(tasksToEdit.getBody());

        setUpSpinners();

    }

    private void setUpSpinners(){
        taskTeamSpinner = (Spinner) findViewById(R.id.editTeamSpinner);

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
                        taskTeamSpinner.setAdapter(new ArrayAdapter<>(
                                this,
                                (android.R.layout.simple_spinner_item),
                                teamName
                        ));
                        taskTeamSpinner.setSelection(getSpinnerIndex(taskTeamSpinner, tasksToEdit.getTeamTask().getName()));
                    });
                },
                failure -> {
                    teamFuture.complete(null);
                    Log.i(TAG, "Did not read any team");
                }
        );

        taskStateSpinner = (Spinner) findViewById(R.id.editStateSpinner);

        taskStateSpinner.setAdapter(new ArrayAdapter<>(
                this,
                (android.R.layout.simple_spinner_item),
                State.values()
        ));

        taskStateSpinner.setSelection(getSpinnerIndex(taskStateSpinner, tasksToEdit.getState().toString()));
    }

    private int getSpinnerIndex(Spinner spinner, String stringValToCheck){
        for (int i = 0; i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(stringValToCheck)) {
                return  i;
            }
        }
        return 0;
    }

    private void setUpSave() {
        Button saveEdit = (Button) findViewById(R.id.saveEdit);

        saveEdit.setOnClickListener(v -> {
            List<Team> teamsList = null;
            String teamToSaveString = taskTeamSpinner.getSelectedItem().toString();

            Log.i(TAG, teamToSaveString+ "gggg");

            try {
                teamsList = teamFuture.get();
            }catch (InterruptedException ie){
                Log.e(TAG, "InterruptedException while getting task");
                Thread.currentThread().interrupt();
            }catch (ExecutionException ee){
                Log.e(TAG, "ExecutionException while getting task");
            }

            Team teamToSave = teamsList.stream().filter(c -> c.getName().equals(teamToSaveString)).findAny().orElseThrow(RuntimeException :: new);

            Task tasksToSave = Task.builder()
                    .title(titleEditText.getText().toString())
                    .id(tasksToEdit.getId())
                    .dateCreated(tasksToEdit.getDateCreated())
                    .body(descriptionEditText.getText().toString())
                    .teamTask(teamToSave)
                    .state(taskStateFromString(taskStateSpinner.getSelectedItem().toString()))
                    .build();

            Log.i(TAG, String.valueOf(tasksToSave));
            Log.i(TAG, "tassskkk"+tasksToSave.toString());


            //f8f38ae9-854f-4b14-bdb6-0039d7559b59
            Amplify.API.mutate(
                    ModelMutation.update(tasksToSave),
                    successRes -> {
                        Log.i(TAG, "EditTaskActivity.setUpSave(): update a task successfully" + successRes.toString());
                        Snackbar.make(findViewById(R.id.editTaskActivity), "Updated Task :D", Snackbar.LENGTH_LONG).show();
                    },
                    failureRes -> Log.i(TAG, "EditTaskActivity.setUpSave(): failed" + failureRes)
            );
        });
    }

    public static State taskStateFromString(String inputTaskStateText){
        for (State state : State.values()){
            if (state.toString().equalsIgnoreCase(inputTaskStateText)){
                return state;
            }
        }
        return null;
    }

    private void setupDelete(){
        Button delete = (Button) findViewById(R.id.deleteTask);

        delete.setOnClickListener(v -> {
            Amplify.API.mutate(
                    ModelMutation.delete(tasksToEdit),
                    successRes -> {
                        Log.i(TAG, "Deleted task successfully" + successRes.toString());
                        Intent goToTaskHome = new Intent(EditTaskActivity.this, MainActivity.class);
                        startActivity(goToTaskHome);
                    },
                    failure -> Log.i(TAG, "Did not delete anything idk why" + failure)
            );
        });
    }
}