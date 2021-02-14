package com.example.langapp.entities;

public class Task {
    private String name;
    private Integer duration;
    private int day;
    private String users;

    public Task() {
    }

    public Task(String name, Integer duration, int day, String users) {
        this.name = name;
        this.duration = duration;
        this.day = day;
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }
}
