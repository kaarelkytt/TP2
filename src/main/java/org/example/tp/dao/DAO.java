package org.example.tp.dao;

import org.example.tp.dataobjects.Exercise;
import org.example.tp.dataobjects.Session;
import org.example.tp.dataobjects.Workout;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DAO {

    private final EntityManagerFactory emf;
    private final EntityManager em;
    private final Properties properties;

    private ObservableList<Workout> sessionWorkouts = FXCollections.observableArrayList();
    private ObservableList<Workout> oldSessionWorkouts = FXCollections.observableArrayList();


    public DAO() throws IOException {
        emf = Persistence.createEntityManagerFactory("tp");
        em = emf.createEntityManager();

        try (FileInputStream in = new FileInputStream("src\\main\\resources\\config.properties")) {
            properties = new Properties();
            properties.load(in);
        }
    }

    public List<Exercise> getExercises() {
        TypedQuery<Exercise> query = em.createQuery("SELECT e FROM Exercise e", Exercise.class);
        return query.getResultList();
    }

    public Exercise getExerciseById(int id) {
        for (Exercise exercise : getExercises()) {
            if (exercise.getId() == id) {
                return exercise;
            }
        }
        return null;
    }

    public List<Session> getAllSessions() {
        TypedQuery<Session> query = em.createQuery("SELECT s FROM Session s ORDER BY s.dateTime DESC", Session.class);
        return query.getResultList();
    }

    public List<Session> getDateSessions(LocalDate date) {
        TypedQuery<Session> query = em.createQuery("SELECT s FROM Session s WHERE s.dateTime BETWEEN :start AND :end ORDER BY s.dateTime DESC", Session.class);
        query.setParameter("start", date.atStartOfDay());
        query.setParameter("end", date.atTime(23, 59, 59));
        return query.getResultList();
    }

    public ObservableList<Workout> getSessionWorkouts() {
        return sessionWorkouts;
    }

    public ObservableList<Workout> getOldSessionWorkouts() {
        return oldSessionWorkouts;
    }

    public Workout getSessionWorkout(Exercise exercise) {
        for (Workout workout : getSessionWorkouts()) {
            if (workout.getExercise() == exercise) {
                return workout;
            }
        }
        return null;
    }

    public void setSessionWorkouts(ObservableList<Workout> sessionWorkouts) {
        this.sessionWorkouts = sessionWorkouts;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) throws IOException {
        try (FileOutputStream out = new FileOutputStream("src\\main\\resources\\config.properties")){
            properties.setProperty(key, value);
            properties.store(out, null);
        }
    }

    public boolean isAddedToSessionWorkouts(Exercise exercise) {
        for (Workout workout : getSessionWorkouts()) {
            if (workout.getExercise().equals(exercise)) {
                return true;
            }
        }
        return false;
    }

    public Workout findLastWorkout(Exercise exercise) {
        for (Session session : getAllSessions()) {
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
        for (Session session : getAllSessions()) {
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

    public void saveSession(Session session) {
        em.getTransaction().begin();
        for (Workout workout : session.getWorkouts()) {
            em.persist(workout);
        }
        em.persist(session);
        em.getTransaction().commit();
    }

    public void saveSessions(List<Session> sessions) {
        em.getTransaction().begin();
        for (Session session : sessions) {
            for (Workout workout : session.getWorkouts()) {
                em.persist(workout);
            }
            em.persist(session);
        }
        em.getTransaction().commit();
    }

    public void saveExercises(List<Exercise> exercises) {
        em.getTransaction().begin();
        for (Exercise exercise : exercises) {
            em.persist(exercise);
        }
        em.getTransaction().commit();
    }

    public long findEstimatedDuration(){
        long estimatedDuration = 0;
        for (Workout workout : sessionWorkouts) {
            estimatedDuration += findAverageExerciseDuration(workout.getExercise());
        }
        return estimatedDuration;
    }

    public String averageDurationString(Exercise exercise){
        long duration = findAverageExerciseDuration(exercise);
        return String.format("%02d", duration / 1000 / 60) + ":" + String.format("%02d", duration / 1000 % 60);
    }

    private long findAverageExerciseDuration(Exercise exercise){
        List<Workout> workouts = findAllWorkouts(exercise);

        long totalDuration = 0;
        for (Workout workout : workouts) {
            totalDuration += workout.getDuration();
        }

        if (!workouts.isEmpty()){
            return totalDuration / workouts.size();
        } else{
            return 600000; // default 10 minutes
        }
    }

    public Long getMaxSessionId() {
        TypedQuery<Long> query = em.createQuery("SELECT MAX(s.id) FROM Session s", Long.class);
        Long maxId = query.getSingleResult();
        return maxId != null ? maxId : 0L;
    }

    public Long getMaxWorkoutId() {
        TypedQuery<Long> query = em.createQuery("SELECT MAX(w.id) FROM Workout w", Long.class);
        Long maxId = query.getSingleResult();
        return maxId != null ? maxId : 0L;
    }

    public void beginTransaction(){
        em.getTransaction().begin();
    }

    public void endTransaction(){
        em.getTransaction().commit();
    }

    public void close() {
        em.close();
        emf.close();
    }
}




