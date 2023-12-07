package com.tasks.taskmanager.activity;

import static com.tasks.taskmanager.activity.MainActivity.TASK_ID_TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.tasks.taskmanager.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TaskDetails extends AppCompatActivity {

    public String taskImg;

    private MediaPlayer mp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Button back = findViewById(R.id.backDetails);

        back.setOnClickListener(v -> {
            Intent goBack = new Intent(TaskDetails.this, MainActivity.class);
            startActivity(goBack);
        });


        mp = new MediaPlayer();

        Intent intent = getIntent();
        String taskTitle = intent.getStringExtra("taskTitle");
        String taskBody = intent.getStringExtra("taskBody");
        String taskState = intent.getStringExtra("taskState");
        String teamTask = intent.getStringExtra("teamName");
        taskImg = intent.getStringExtra("taskS3Uri");
        String taskLatitude = intent.getStringExtra("taskLatitude");
        String taskLongitude = intent.getStringExtra("taskLatitude");

        TextView titleTextView = findViewById(R.id.taskDetailTitle);
        titleTextView.setText(taskTitle);

        TextView stateTextView = findViewById(R.id.taskState);
        stateTextView.setText(taskState);

        TextView descriptionTextView = findViewById(R.id.taskDetailDescription);
        descriptionTextView.setText(taskBody);

        TextView teamTaskTextView = findViewById(R.id.teamTask);
        teamTaskTextView.setText(teamTask);

        TextView taskLatitudeTextView = findViewById(R.id.textViewForLatitude);
        taskLatitudeTextView.setText("Latitude: "+taskLatitude);

        TextView taskLongitudeTextView = findViewById(R.id.textViewForLongitude);
        taskLongitudeTextView.setText("Longitude: "+taskLongitude);

        updateUI();
        setUpSpeakButton();
    }

    private void updateUI() {
        if (taskImg != null) {
            ImageView image = findViewById(R.id.imageViewDetails);
            Amplify.Storage.downloadFile(
                    taskImg,
                    new File(getApplicationContext().getFilesDir(), "downloaded_image.jpg"),
                    result -> {
                        Log.i("MyAmplifyApp", "Successfully downloaded: " + result.getFile().getPath());
                        runOnUiThread(() -> {
                            Bitmap bitmap = BitmapFactory.decodeFile(result.getFile().getPath());
                            image.setImageBitmap(bitmap);
                        });
                    },
                    error -> Log.e("MyAmplifyApp", "Download failed", error)
            );
        }
    }

    private void setUpSpeakButton(){
        Button speakButton = (Button) findViewById(R.id.convertTextToSpeach);

        speakButton.setOnClickListener(b -> {
            String taskBody = ((TextView) findViewById(R.id.taskDetailDescription)).getText().toString();

            Amplify.Predictions.convertTextToSpeech(
                    taskBody,
                    result ->playAudio(result.getAudioData()),
                    error -> Log.e("TAG", "conversion failed")
            );

        });
    }


    private void playAudio(InputStream data) {
        File mp3File = new File(getCacheDir(), "audio.mp3");

        try (OutputStream out = new FileOutputStream(mp3File)) {
            byte[] buffer = new byte[8 * 1_024];
            int bytesRead;
            while ((bytesRead = data.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            mp.reset();
            mp.setOnPreparedListener(MediaPlayer::start);
            mp.setDataSource(new FileInputStream(mp3File).getFD());
            mp.prepareAsync();
        } catch (IOException error) {
            Log.e("MyAmplifyApp", "Error writing audio file", error);
        }
    }

}
