package org.example.tp.dataobjects;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.example.tp.logic.Tab.capitalize;

@Entity
public class Session {
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private LocalDateTime dateTime;
    private long duration;
    private String comment;
    @OneToMany
    private final List<Workout> workouts = new ArrayList<>();

    public Session(long id, String name, LocalDateTime dateTime, long duration, String comment) {
        this.id = id;
        this.name = name;
        this.dateTime = dateTime;
        this.duration = duration;
        this.comment = comment;
    }

    public Session(long id, String name, LocalDateTime dateTime, int duration) {
        this.id = id;
        this.name = name;
        this.dateTime = dateTime;
        this.duration = duration;
    }

    public Session(long id, LocalDateTime dateTime) {
        this.id = id;
        this.dateTime = dateTime;
    }

    public Session() {
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

    public long getId() {
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

    public String getMuscleGroups() {
        Set<String> muscleGroups = new HashSet<>();
        for (Workout workout : workouts) {
            muscleGroups.add(capitalize(workout.getExercise().getCategory().name()));
        }

        return String.join(", ", muscleGroups);
    }
}