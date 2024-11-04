package org.example.tp.dataobjects;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private final int id;
    private String name;
    private final LocalDateTime dateTime;
    private long duration;
    private String comment;
    private final List<Workout> workouts = new ArrayList<>();

    public Session(int id, String name, LocalDateTime dateTime, int duration) {
        this.id = id;
        this.name = name;
        this.dateTime = dateTime;
        this.duration = duration;
    }

    public Session(int id, LocalDateTime dateTime) {
        this.id = id;
        this.dateTime = dateTime;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public long getDuration() {
        return duration;
    }

    public String getComment() {
        return comment;
    }

    public void addWorkout(Workout workout) {
        workouts.add(workout);
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateTime=" + dateTime +
                ", duration=" + duration +
                ", comment='" + comment + '\'' +
                ", workouts=" + workouts +
                '}';
    }
}