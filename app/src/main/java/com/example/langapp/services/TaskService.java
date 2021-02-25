package com.example.langapp.services;

import com.example.langapp.entities.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TaskService {
    @GET("tasksForDay")
    Call<List<Task>> getTasksForDay(@Query("day") int day);
}
