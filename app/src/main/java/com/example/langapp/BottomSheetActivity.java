package com.example.langapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetActivity extends BottomSheetDialogFragment {

    //DATABASE REFERENCE
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reff = database.getReference().child("Task");
    Button newTask;
    //LIST FOR DATA INPUT
//    List<Task> taskList = new ArrayList<>();
    //EVIDENCE NUMBER OF AUDIO RECORDED FOR THAT DAY
    int audioNumber = 0;
    List<Task> taskList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_bottom_sheet,
                container, false);

        Button button  = (Button) v.findViewById(R.id.newTask);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), PopUpDialog.class);
                startActivity(intent);
            }
        });
//        readTasksForDay(new FirebaseCallback() {
//            @Override
//            public void onCallback(List<Task> list, int lastRecorded) {
//                taskList = list;
//                audioNumber = lastRecorded + 1;
//            }
//        });

//         @SuppressLint("ResourceType") LinearLayout bottomLayout = (LinearLayout) getView().findViewById(R.layout.layout_bottom_sheet);
        LinearLayout bottomLayout = (LinearLayout) inflater.inflate(R.layout.layout_bottom_sheet,container,false);
        System.out.println(this.taskList.size());
        if(taskList.size()>0){
            for (Task task : taskList) {
                System.out.println(task.getName());
                Button taskButton = new Button(getActivity());
                taskButton.setText(task.getName());
                bottomLayout.addView(taskButton);
            }

        }
        else{
            //TODO postavi textView na kome pise nema kreiranih taskova za danasnji dan
        }        return v;
    }


    //POZIV DB
    private interface FirebaseCallback{
        void onCallback(List<Task> taskList, int lastRecorded);
    }
    private void readTasksForDay(BottomSheetActivity.FirebaseCallback firebaseCallback){
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int lastRecorded = 1;
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Task task = postSnapshot.getValue(Task.class);
                    taskList.add(task);
                    System.out.println(task.getName());
                }
                firebaseCallback.onCallback(taskList, taskList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("The read failed: " ,error.getMessage());
            }
        });
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }
}
