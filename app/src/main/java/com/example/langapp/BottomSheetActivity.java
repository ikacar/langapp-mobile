package com.example.langapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.langapp.entities.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BottomSheetActivity extends BottomSheetDialogFragment {

    //DATABASE REFERENCE
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reff = database.getReference().child("Task");
    Button newTask;
    int day;
    //EVIDENCE NUMBER OF AUDIO RECORDED FOR THAT DAY
    int audioNumber = 0;
    List<Task> taskList;
    //TODO put folder path in .properties file
    String audioPath;
    MediaPlayer mediaPlayer ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //DB call - gett all created tasks for spesific day
        taskList = getTasksForDay(this.getDay());

        //Create button views for task list
        for (Task task : taskList) {
            //vreate button view
        }
        View v = inflater.inflate(R.layout.layout_bottom_sheet,
                container, false);




        LinearLayout bottomLayout = (LinearLayout) v.findViewById(R.id.bottom_sheet_layout_container);
        System.out.println(this.taskList.size());
        if(taskList.size()>0){
            for (Task task : taskList) {
                Button taskButton = new Button(getActivity());
                taskButton.setText(task.getName());
                bottomLayout.addView(taskButton);

                taskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO what if i click resume
                        playAudio(task.getName());
//                        if(mediaPlayer.isPlaying()){
//                            stopAudio();
//                        }
//                        else
//                        {
//                            playAudio(task.getName());
//
//                        }
                    }
                });
            }

        }
        else{
            //TODO postavi textView na kome pise nema kreiranih taskova za danasnji dan
        }
        Button button  = (Button) v.findViewById(R.id.newTask);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), PopUpDialog.class);
                startActivity(intent);
            }
        });
        return v;
    }
    private List<Task> getTasksForDay(int day){
        //TODO call backend app on amazon or directly amazon database
        List<Task> list = new ArrayList<>();
        list.add(new Task("ime1", 123123, 1, "biljana milica"));
        list.add(new Task("ime2", 543665, 1, "biljana milica"));
        list.add(new Task("ime3", 243599, 2, "ilija milica"));
        list.add(new Task("ime4", 123687, 3, "jovica milica"));
        list.add(new Task("ime5", 123112, 2, "biljana jovanka"));
        list.add(new Task("ime6", 123123, 3, "biljana milica"));
        list.add(new Task("ime7", 543665, 4, "biljana milica"));
        list.add(new Task("ime8", 243599, 4, "ilija milica"));
        list.add(new Task("ime9", 123687, 5, "jovica milica"));
        list.add(new Task("ime10", 123112, 5, "biljana jovanka"));

        List<Task> listNew = new ArrayList<>();

        for (Task task : list) {
            if(task.getDay()==day) listNew.add(task);
        }
        return listNew;
    }
    private void playAudio(String audioFile){
        System.out.println("audio " + audioFile);
        boolean canPlay = true; // false if could not download audio

        String path = audioPath +"/"+audioFile;
        File file = new File(path);
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
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        }

    }


    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
