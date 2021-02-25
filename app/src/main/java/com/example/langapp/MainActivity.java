package com.example.langapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.langapp.entities.Task;
import com.example.langapp.services.TaskService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.usernameText);
        password = findViewById(R.id.passwordText);

        String url = "";
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://localhost:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TaskService taskService = retrofit.create(TaskService.class);
        Call<List<Task>> call = taskService.getTasksForDay(1);
        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                    if(!response.isSuccessful()){
                        System.out.println("response je los");
                    }
                    else{
                        List<Task> taskovi  = response.body();

                        for (Task task : taskovi) {
                            System.out.println(task.getName());
                        }
                    }

            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                System.out.println("nisam uspeo");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            System.out.println("vec je ulogovan");
        }
    }

    public void signIn(View view){
//        String em = email.getText().toString();
//        String pass = password.getText().toString();s
        String em = "zaekonomiku@gmail.com";
        String pass = "zaekonomiku123";
        mAuth.signInWithEmailAndPassword(em, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                }
                else{
                    System.out.println("neuspesno logovanje");
                    Toast.makeText(MainActivity.this, "Login Unsuccesfull", Toast.LENGTH_SHORT);
                }
            }
        });

    }
}