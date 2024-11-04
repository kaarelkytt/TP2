package org.example.tp.dao;

import org.example.tp.dataobjects.Exercise;
import org.example.tp.dataobjects.Session;
import org.example.tp.dataobjects.Workout;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.tp.logic.FromFile;
import org.example.tp.logic.ToFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DAO {

    private final List<Exercise> exercises;
    private final List<Session> sessions;
    private ObservableList<Exercise> sessionExercises = FXCollections.observableArrayList();
    private ObservableList<Workout> sessionWorkouts = FXCollections.observableArrayList();

    public DAO() throws IOException {
        this.exercises = FromFile.importExercises("src\\main\\resources\\org\\example\\tp\\data\\exercises.txt");
        this.sessions = FromFile.importHistory("src\\main\\resources\\org\\example\\tp\\data\\history.txt", this);
    }

    public Exercise getExerciseById(int id) {
        for (Exercise exercise : exercises) {
            if (exercise.getId() == id) {
                return exercise;
            }
        }
        return null;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public ObservableList<Exercise> getSessionExercises() {
        return sessionExercises;
    }

    public void setSessionExercises(ObservableList<Exercise> sessionExercises) {
        this.sessionExercises = sessionExercises;
    }

    public ObservableList<Workout> getSessionWorkouts() {
        return sessionWorkouts;
    }

    public void setSessionWorkouts(ObservableList<Workout> sessionWorkouts) {
        this.sessionWorkouts = sessionWorkouts;
    }

    public Workout findLastWorkout(Exercise exercise) {
        for (Session session : sessions) {
            for (Workout workout : session.getWorkouts()) {
                if (workout.getExercise().getId() == exercise.getId()) {
                    return workout;
                }
            }
        }
        return null;
    }

    public List<Workout> findAllWorkouts(Exercise exercise) {
        ArrayList<Workout> allWorkouts = new ArrayList<>();
        for (Session session : sessions) {
            for (Workout workout : session.getWorkouts()) {
                if (workout.getExercise().getId() == exercise.getId()) {
                    allWorkouts.add(workout);
                }
            }
        }
        return allWorkouts;
    }

    public void removeFromSessionWorkouts(Exercise exercise){
        for (Workout workout : sessionWorkouts) {
            if (workout.getExercise().getId() == exercise.getId()){
                sessionWorkouts.remove(workout);
                break;
            }
        }
    }

    public void saveSession(Session session) throws IOException {
        sessions.add(session);
        ToFile.addSession("src\\main\\resources\\org\\example\\tp\\data\\history.txt", session);
    }

    public long findEstimatedDuration(){
        long estimatedDuration = 0;
        for (Exercise exercise : sessionExercises) {
            Workout lastWorkout = findLastWorkout(exercise);
            if (lastWorkout == null){
                estimatedDuration += 600;
            } else {
                estimatedDuration += lastWorkout.getDuration();
            }
        }
        return estimatedDuration*1000;
    }
}
