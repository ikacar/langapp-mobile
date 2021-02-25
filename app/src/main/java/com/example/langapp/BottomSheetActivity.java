package com.example.langapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.langapp.entities.Task;
import com.example.langapp.services.TaskService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BottomSheetActivity extends BottomSheetDialogFragment {

    //DATABASE REFERENCE
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reff = database.getReference().child("Task");
    Button newTask;
    int day;
    //EVIDENCE NUMBER OF AUDIO RECORDED FOR THAT DAY
    int audioNumber = 0;
    List<Task> taskList  = new ArrayList<>();
    //TODO put folder path in .properties file
    MediaPlayer mediaPlayer ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.layout_bottom_sheet, container, false);

        //DB call - gett all created tasks for spesific day
        getTasksForDay(this.getDay(), layoutView,inflater);

        //Create button views for task list
//        for (Task task : taskList) {
//            //vreate button view
//        }

//        LinearLayout bottomLayout = (LinearLayout) layoutView.findViewById(R.id.bottom_sheet_layout_container);
//        System.out.println("sss" + this.getTaskList().size());
//        if(getTaskList()!=null && getTaskList().size()>0){
//            for (Task task : taskList) {
//                LinearLayout taskLayout = createTask(inflater, task);
//                bottomLayout.addView(taskLayout);
//
//            }
//
//        }
//        else{
//            //TODO postavi textView na kome pise nema kreiranih taskova za danasnji dan
//        }
//        Button button  = (Button) layoutView.findViewById(R.id.newTask);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(getContext(), PopUpDialog.class);
//                startActivity(intent);
//            }
//        });
        return layoutView;
    }
    private void getTasksForDay(int day, View layoutView, LayoutInflater inflater){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://192.168.0.17:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TaskService taskService = retrofit.create(TaskService.class);
        Call<List<Task>> call = taskService.getTasksForDay(day);
        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if(!response.isSuccessful()){
                    System.out.println("response je los");
                }
                else{
                     taskList= response.body();
                    showTasks(taskList,layoutView,inflater);
                }

            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {

                System.out.println("nisam uspeo " + t.getMessage());
            }
        });

    }
    private void showTasks(List<Task> allTasks, View layoutView, LayoutInflater inflater){
        LinearLayout bottomLayout = (LinearLayout) layoutView.findViewById(R.id.bottom_sheet_layout_container);
        System.out.println("sss" + this.getTaskList().size());
        if(getTaskList()!=null && getTaskList().size()>0){
            for (Task task : allTasks) {
                LinearLayout taskLayout = createTask(inflater, task);
                bottomLayout.addView(taskLayout);

            }

        }
        else{
            //TODO postavi textView na kome pise nema kreiranih taskova za danasnji dan
        }
        Button button  = (Button) layoutView.findViewById(R.id.newTask);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), PopUpDialog.class);
                startActivity(intent);
            }
        });
    }
    private LinearLayout createTask(LayoutInflater inflater, Task task){
        View playButton, pauseButton;
        TextView taskName, taskDuration, taskPosition;
        SeekBar seekBar;
        Handler handler = new Handler();

        //NEW LAYOUT FOR TASK
        LinearLayout taskLayout =(LinearLayout) inflater.inflate(R.layout.task_layout_sheet,null);

        playButton = taskLayout.findViewById(R.id.bt_play);
        pauseButton = taskLayout.findViewById(R.id.bt_pause);
        taskName = taskLayout.findViewById(R.id.task_name);
        taskDuration = taskLayout.findViewById(R.id.task_duration);
        taskPosition = taskLayout.findViewById(R.id.task_position);
        seekBar = taskLayout.findViewById(R.id.seek_bar);

        //STATIC FIELDS
        taskDuration.setText(task.getDuration().toString());
        taskName.setText(task.getName());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        };

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("audio " + task.getName());
                boolean canPlay = true; // false if could not download audio

                //TODO VRATITI

                String folderPath = getContext().getExternalFilesDir(null).toString();
                File folder = new File(folderPath);
                String taskPath = getContext().getExternalFilesDir(null).toString()  +"/" +  task.getName();
                System.out.println(folderPath);

                if(!folder.exists()){
                    System.out.println("nema novog foldera - pravim");
                    folder.mkdir();
                }

                File file = new File(taskPath);
                if(!file.exists()){

                    System.out.println("download file from storage");
                    //TODO
                    // else download from storage and play
                    // call Rest api for retrieving audio file
                    // canPlay false if cont download
                    // maybe check file.exists once again?
                    canPlay = false;
                }
                if(canPlay){
                    playButton.setVisibility(View.GONE);
                    pauseButton.setVisibility(View.VISIBLE);
                    int id = 1;
                    mediaPlayer = new MediaPlayer();

                    //TODO VRATITI
                    try {
                        mediaPlayer.setDataSource(taskPath);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        System.out.println("ne moze da pokrene mediaplayer");
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration());
                    handler.postDelayed(runnable,0);

                }

            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
                mediaPlayer.pause();
                handler.removeCallbacks(runnable);
            }
        });
        return taskLayout;
    }
    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }
}
