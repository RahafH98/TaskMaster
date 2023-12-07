package com.tasks.taskmanager.activity.adapter;

import static com.tasks.taskmanager.activity.MainActivity.TASK_ID_TAG;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Task;
import com.tasks.taskmanager.R;
import com.tasks.taskmanager.activity.EditTaskActivity;
import com.tasks.taskmanager.activity.TaskDetails;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TasksListRecyclerViewAdapter extends RecyclerView.Adapter<TasksListRecyclerViewAdapter.TasksListViewHolder> {

    List<Task> tasks;

    Context callingActivity;

    public TasksListRecyclerViewAdapter(List<Task> tasks, Context callingActivity) {
        this.tasks = tasks;
        this.callingActivity = callingActivity;
    }

    @NonNull
    @Override
    public TasksListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tasksFragment = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_tasks_list, parent, false);

        return new TasksListViewHolder(tasksFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksListViewHolder holder, int position) {

        TextView taskFragmentTextViewTitle = (TextView) holder.itemView.findViewById(R.id.listFragmentTextViewTitle);
        TextView taskFragmentTextViewDate = (TextView) holder.itemView.findViewById(R.id.listFragmentTextViewDate);
        TextView taskFragmentTextViewState = (TextView) holder.itemView.findViewById(R.id.listFragmentTextViewState);
        TextView taskFragmentTextViewTeam = (TextView) holder.itemView.findViewById(R.id.listFragmentTextViewTeam);


        DateFormat dateCreatedIso8061InputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateCreatedIso8061InputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat dateCreatedOutputFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateCreatedOutputFormat.setTimeZone(TimeZone.getDefault());
        String dateCreatedString = " ";

        try {
            {
            Date dateCreatedJavaDate = dateCreatedIso8061InputFormat.parse(tasks.get(position).getDateCreated().format());
            if (dateCreatedJavaDate != null){
                dateCreatedString = dateCreatedOutputFormat.format(dateCreatedJavaDate);
            }
        }
        }catch (ParseException e){
            throw new RuntimeException(e);
        }

        String taskTitle = tasks.get(position).getTitle();
        String taskBody = tasks.get(position).getBody();
        String taskDate = dateCreatedString;
        String taskState = tasks.get(position).getState().toString();
        String teamName = tasks.get(position).getTeamTask().getName();
        String taskImg = tasks.get(position).getTaskS3Uri();
        String taskLatitude = tasks.get(position).getTaskLatitude();
        String taskLongitude = tasks.get(position).getTaskLongitude();


        taskFragmentTextViewTitle.setText(taskTitle);
        taskFragmentTextViewDate.setText(taskDate);
        taskFragmentTextViewState.setText(taskState);
        taskFragmentTextViewTeam.setText(teamName);



        View tasksViewHolder = holder.itemView;
        tasksViewHolder.setOnClickListener(v -> {
            Intent goToTaskDetailsIntent = new Intent(callingActivity, TaskDetails.class);
            //            goToTaskDetailsIntent.putExtra(TASK_ID_TAG, tasks.get(position).getId());
            goToTaskDetailsIntent.putExtra("taskTitle", taskTitle);
            goToTaskDetailsIntent.putExtra("taskBody", taskBody);
            goToTaskDetailsIntent.putExtra("taskDate", taskDate);
            goToTaskDetailsIntent.putExtra("taskState", taskState);
            goToTaskDetailsIntent.putExtra("teamName", teamName);
            goToTaskDetailsIntent.putExtra("taskS3Uri", taskImg);
            goToTaskDetailsIntent.putExtra("taskLatitude", taskLatitude);
            goToTaskDetailsIntent.putExtra("taskLongitude", taskLongitude);

            //here if i want it to go to the edit page when it click on the task
//            Intent goToTaskDetailsIntent = new Intent(callingActivity, EditTaskActivity.class);
//            goToTaskDetailsIntent.putExtra(TASK_ID_TAG, tasks.get(position).getId());
//            goToTaskDetailsIntent.putExtra("taskTitle", taskTitle);
//            goToTaskDetailsIntent.putExtra("taskBody", taskBody);
//            goToTaskDetailsIntent.putExtra("taskDate", taskDate);
//            goToTaskDetailsIntent.putExtra("taskState", taskState);
//            goToTaskDetailsIntent.putExtra("teamName", teamName);


            callingActivity.startActivity(goToTaskDetailsIntent);
        });

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TasksListViewHolder extends RecyclerView.ViewHolder{

        public TasksListViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
