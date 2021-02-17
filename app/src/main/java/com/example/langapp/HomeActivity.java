package com.example.langapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.langapp.entities.Task;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.media.MediaPlayer;
import android.widget.Toast;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class HomeActivity extends AppCompatActivity {
    //TODO pomeriti odavle
    public static final int RequestPermissionCode = 1;

    String filePath = null;
    String fileName = null;
    MediaRecorder mediaRecorder = null;
    MediaPlayer mediaPlayer ;
    List<Task> taskList = new ArrayList<>();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //TODO da li tereba
    DatabaseReference reff = database.getReference().child("Task");
    //        TextView day = (TextView)v;



    int audioNumber = 1;
    int duration = 0;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mStorage =  FirebaseStorage.getInstance().getReference();
//        Task task1  = new Task();
//        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
//        task1.setName("fileName");
//        task1.setDay(2);
//        task1.setDuration(123);
//        task1.setUsers("ilija biljana");
//        reff.push().setValue(task1);
;


        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Task task = postSnapshot.getValue(Task.class);
                    System.out.println("tasak " + task.getName());
                    taskList.add(task);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("The read failed: " ,error.getMessage());
            }
        });



        //uzmi rezultate sa firebase-a
        //uzmi samo koji fale ili uzmi sve?

        //pokupi sve


    }
    public void openBottomSheet(View v){


        LinearLayout bottomLayout = (LinearLayout) findViewById(R.id.homeLayout);
        System.out.println("count " + taskList.size());
        for (Task task : taskList) {
            System.out.println("zez "  +task.getName());
            TextView textView = new TextView(this);
            textView.setText(task.getName()+ " " + task.getDay());
            bottomLayout.addView(textView);
        }

        //TODO posalji iz bottomsheet-a poslednji snimljen pa dodaj + 1
//        int audioNumber = lastRecorded +1;
        int day = Integer.parseInt(((TextView)v).getText().toString());


        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                HomeActivity.this, R.style.BottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.layout_bottom_sheet,
                        (LinearLayout)findViewById(R.id.bottomSheetContainer)
                );
        Button buttonStart = (Button) bottomSheetView.findViewById(R.id.startButton);
        Button buttonStop = (Button) bottomSheetView.findViewById(R.id.stopButton);
        Button buttonPlayLastRecordAudio = (Button) bottomSheetView.findViewById(R.id.playButton);
        Button buttonUpload = (Button) bottomSheetView.findViewById(R.id.uploadButton);

        //******************************
        //******************************

        //TODO Pozovi taskService za taj dan i napuni mapu sa kojom ces posle da generises tabelu
//        List<Task> taskList = taskService.findAllTasksForDay(day);
        //TODO za svaki task iz taskList kreiraj jedan red u tabeli

        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonUpload.setEnabled(false);

        bottomSheetView.findViewById(R.id.exitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkPermission()) {
                    fileName = "Audio-"+day+"-"+audioNumber+".3gp";
                    filePath = getExternalFilesDir(null) + "/Audio/"  + fileName;

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);

                    Toast.makeText(HomeActivity.this, "Recording started",
                            Toast.LENGTH_LONG).show();
                } else {
                    requestPermission();
                }

            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonUpload.setEnabled(false);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                duration = mediaPlayer.getDuration();

                Toast.makeText(HomeActivity.this, "Recording Completed",
                        Toast.LENGTH_LONG).show();
            }
        });
        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonUpload.setEnabled(true);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(filePath);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                Toast.makeText(HomeActivity.this, "Recording Playing",
                        Toast.LENGTH_LONG).show();
            }
        });
        bottomSheetView.findViewById(R.id.uploadButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert into task table
                Task task  = new Task();
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                task.setName(fileName);
                task.setDay(day);
                task.setDuration(duration);
                task.setUsers("ilija biljana");
                reff.push().setValue(task);

                //upload on storage
                StorageReference filepath = mStorage.child("Audio").child(fileName);
                Uri uri = Uri.fromFile(new File(filePath));
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });


            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(filePath);
    }

//    public String CreateRandomAudioFileName(int string){
//        StringBuilder stringBuilder = new StringBuilder( string );
//        int i = 0 ;
//        while(i < string ) {
//            stringBuilder.append(RandomAudioFileName.
//                    charAt(random.nextInt(RandomAudioFileName.length())));
//
//            i++ ;
//        }
//        return stringBuilder.toString();
//    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(HomeActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(HomeActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(HomeActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}