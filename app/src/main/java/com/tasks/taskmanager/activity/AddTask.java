package com.tasks.taskmanager.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.health.connect.datatypes.ExerciseRoute;
import android.location.Geocoder;
import android.net.LocalServerSocket;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.appsync.ModelMetadata;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.State;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.snackbar.Snackbar;
import com.tasks.taskmanager.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AddTask extends AppCompatActivity {

    public static final String TAG = "gggAddTaskActivity";
    static final int LOCATION_POLLING_INTERVAL = 5 * 1000;

    Spinner teamSpinner = null;

    Spinner taskStateSpinner = null;

    CompletableFuture<List<Team>> teamFuture = new CompletableFuture<>();

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ImageView selectedImageView;
    private String filePath;

    FusedLocationProviderClient locationProviderClient = null;

    private String currentLatitude;

    private String currentLongitude;

    Geocoder geocoder = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Button back = (Button) findViewById(R.id.backButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBack = new Intent(AddTask.this, MainActivity.class);
                startActivity(goBack);
            }
        });

//        Spinner taskStateSpinner = (Spinner) findViewById(R.id.stateSpinner);
//        taskStateSpinner.setAdapter(new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_spinner_item,
//                State.values()));

        teamFuture = new CompletableFuture<>();

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        locationProviderClient.flushLocations();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                Log.e(TAG, "Location CallBack was null");
            }

            currentLatitude = Double.toString(location.getLatitude());
            currentLongitude = Double.toString(location.getLongitude());

            Log.i(TAG, "our user Latitude" + location.getLatitude());
            Log.i(TAG, "our user Longitude" + location.getLongitude());
        });

        locationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        });

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_POLLING_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallBack = new LocationCallback(){
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                try {
                    String address = geocoder.getFromLocation(
                            locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude(),
                            1)
                            .get(0)
                            .getAddressLine(0);
                    Log.i(TAG, "Repeating current location is: "+ address);
                }catch (IOException ioe){
                    Log.e(TAG, "could not get user location" + ioe.getMessage(), ioe);
                }
            }
        };

        locationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, getMainLooper());

        setTeamSpinner();
        setUpAddButton();

        selectedImageView = findViewById(R.id.imageView3);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        try {
                            selectedImageView.setImageURI(uri);
                            filePath = getRealPathFromURI(uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("PHTACT", "Error setting image URI: " + e.getMessage());
                        }
                    } else {
                        Log.d("PHTACT", "No media selected");
                        filePath = null;
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent callingIntent = getIntent();

        if (callingIntent != null) {
            Log.i(TAG, "Received intent with type: " + callingIntent.getType());

            if (callingIntent.getType() != null && callingIntent.getType().equals("text/plain")) {
                String callingText = callingIntent.getStringExtra(Intent.EXTRA_TEXT);
                if (callingText != null) {
                    String cleanText = cleanText(callingText);
                    ((TextView) findViewById(R.id.addTaskInput)).setText(cleanText);
                }
            }

            if (callingIntent.getType() != null && callingIntent.getType().startsWith("image/")) {
                Log.i(TAG, "Received image intent");

                Uri incomingImgFileUri = callingIntent.getParcelableExtra(Intent.EXTRA_STREAM);

                if (incomingImgFileUri != null) {
                    Log.i(TAG, "Received image URI: " + incomingImgFileUri.toString());

                    try {
                        InputStream incomingImgFileInputStream = getContentResolver().openInputStream(incomingImgFileUri);

                        ImageView taskImgView = findViewById(R.id.imageView3);

                        if (taskImgView != null) {
                            taskImgView.setImageBitmap(BitmapFactory.decodeStream(incomingImgFileInputStream));
                            Log.i(TAG, "Image set successfully");
                        } else {
                            Log.e(TAG, "ImageView is null");
                        }

                    } catch (IOException io) {
                        Log.e(TAG, "Error handling image: " + io.getMessage(), io);
                    }
                } else {
                    Log.d(TAG, "No image URI received");
                }
            }
        }

        String action = callingIntent.getAction();
        String type = callingIntent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSharedImage(callingIntent);
            }
        }
    }

    private File createTempFile() {
        String fileName = "temp_image";
        File tempDir = getApplicationContext().getCacheDir();
        try {
            return File.createTempFile(fileName, null, tempDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleSharedImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        Log.i(TAG, "imageUri is" + imageUri);
        if (imageUri != null) {
            selectedImageView.setImageURI(imageUri);
            filePath = getFilePathFromUri(imageUri);
            Log.i(TAG, "handleSharedImage: inside the if condition");
            Log.i(TAG, "filepath is " + filePath);
        } else filePath = null;
    }

    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                File tempFile = createTempFile();
                if (tempFile != null) {
                    OutputStream outputStream = new FileOutputStream(tempFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    filePath = tempFile.getAbsolutePath();
                    outputStream.close();
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }


    private String cleanText(String text) {
        text = text.replaceAll("\\b(?:https?|ftp)://\\S+\\b", "");

        text = text.replaceAll("\"", "");

        return text;
    }

    public void onAddImageButtonClicked(View view) {
        if (pickMedia != null) {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        } else {
            Log.e("PhotoPicker", "pickMedia is null");
        }
    }


    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor == null) {
            return null;
        } else {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
    }

    private void setTeamSpinner() {
        teamSpinner = (Spinner) findViewById(R.id.teamSpinner);

        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG, " Reading Teams success");
                    ArrayList<String> teamName = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    for (Team team : success.getData()) {
                        teams.add(team);
                        teamName.add(team.getName());
                    }
                    teamFuture.complete(teams);

                    runOnUiThread(() -> {
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

        taskStateSpinner = (Spinner) findViewById(R.id.stateSpinner);

        taskStateSpinner.setAdapter(new ArrayAdapter<>(
                this,
                (android.R.layout.simple_spinner_item),
                State.values()
        ));
    }

    private void setUpAddButton() {

        Button addTask = (Button) findViewById(R.id.addTask);

        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Your Task is added :D", Snackbar.LENGTH_LONG);
        addTask.setOnClickListener(v -> {

            if (filePath != null && !filePath.isEmpty()) {
                File imageFile = new File(filePath);
                String key = "images/" + imageFile.getName();

                String title = ((EditText) findViewById(R.id.addTaskInput)).getText().toString();
                String body = ((EditText) findViewById(R.id.taskDiscriptionInput)).getText().toString();
//                String currentDate = com.amazonaws.util.DateUtils.formatISO8601Date(new Date());
                String selectedTeamString = teamSpinner.getSelectedItem().toString();

                List<Team> teams = null;
                try {
                    teams = teamFuture.get();
                } catch (InterruptedException ie) {
                    Log.e(TAG, "InterruptedException while getting the teams");
                } catch (ExecutionException ee) {
                    Log.e(TAG, "ExecutionException while getting the teams");
                }

                Team selectedTeam = teams.stream().filter(t -> t.getName().equals(selectedTeamString)).findAny().orElseThrow(RuntimeException::new);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location == null) {
                        Log.e(TAG, "Location CallBack was null");
                    }

                    String currentLatitude = Double.toString(location.getLatitude());
                    String currentLongitude = Double.toString(location.getLongitude());

                    Log.i(TAG, "our user Latitude" + location.getLatitude());
                    Log.i(TAG, "our user Longitude" + location.getLongitude());
                    Task newTask = Task.builder()
                            .title(title)
                            .body(body)
                            .dateCreated(new Temporal.DateTime(new Date(), 0))
                            .state((State) taskStateSpinner.getSelectedItem())
                            .teamTask(selectedTeam)
                            .taskS3Uri(key)
                            .taskLatitude(currentLatitude)
                            .taskLongitude(currentLongitude)
                            .build();

                    Amplify.Storage.uploadFile(key,
                            imageFile,
                            result -> {
                                Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey());
                            },
                            error -> {
                                Log.e("MyAmplifyApp", "Upload failed", error);
                            });

                    Amplify.API.mutate(
                            ModelMutation.create(newTask),
                            successRes -> Log.i(TAG, "AddTaskActivity.onCreate(): made a task successfully"),
                            failureRes -> Log.e(TAG, "AddTaskActivity.onCreate(): failed with this res" + failureRes)
                    );

                    snackbar.show();


                }).addOnCanceledListener(() -> {
                    Log.e(TAG,"location request is canceled");
                }).addOnFailureListener(fail -> {
                    Log.e(TAG, "location request failed" + fail.getMessage(), fail.getCause());
                }).addOnCompleteListener(complete -> {
                    Log.e(TAG, "location request completed");
                });
            }else {
                String title = ((EditText) findViewById(R.id.addTaskInput)).getText().toString();
                String body = ((EditText) findViewById(R.id.taskDiscriptionInput)).getText().toString();
                String currentDate = com.amazonaws.util.DateUtils.formatISO8601Date(new Date());
                String selectedTeamString = teamSpinner.getSelectedItem().toString();

                List<Team> teams = null;
                try {
                    teams = teamFuture.get();
                }catch (InterruptedException ie){
                    Log.e(TAG, "InterruptedException while getting the teams");
                }catch (ExecutionException ee){
                    Log.e(TAG, "ExecutionException while getting the teams");
                }

                Team selectedTeam = teams.stream().filter(t -> t.getName().equals(selectedTeamString)).findAny().orElseThrow(RuntimeException::new);

                Task newTask = Task.builder()
                        .title(title)
                        .body(body)
                        .dateCreated(new Temporal.DateTime(new Date(), 0))
                        .state((State) taskStateSpinner.getSelectedItem())
                        .teamTask(selectedTeam)
                        .build();


                Amplify.API.mutate(
                        ModelMutation.create(newTask),
                        successRes -> Log.i(TAG, "AddTaskActivity.onCreate(): made a task successfully"),
                        failureRes -> Log.e(TAG, "AddTaskActivity.onCreate(): failed with this res" + failureRes)
                );

                snackbar.show();

            }
        });
    }


    public void onDeleteImageButtonClicked(View view) {
        if (filePath != null && !filePath.isEmpty()) {
            File imageFile = new File(filePath);
            Amplify.Storage.remove(
                    imageFile.getName(),
                    result -> {
                        Log.i("MyAmplifyApp", "Successfully deleted: " + result.getKey());
                        filePath = null;
                        selectedImageView.setImageResource(android.R.color.transparent);
                    },
                    error -> {
                        Log.e("MyAmplifyApp", "Deletion failed", error);
                    }
            );
        } else {
            Log.d("MyAmplifyApp", "No image to delete");
        }
    }
}