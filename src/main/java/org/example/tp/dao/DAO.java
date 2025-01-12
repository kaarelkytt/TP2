package org.example.tp.dao;

import org.example.tp.dataobjects.Exercise;
import org.example.tp.dataobjects.Session;
import org.example.tp.dataobjects.Workout;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.tp.logic.FromFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DAO {

    private final EntityManagerFactory emf;
    private final EntityManager em;
    private final Properties properties;

    private ObservableList<Exercise> sessionExercises = FXCollections.observableArrayList();
    private ObservableList<Workout> sessionWorkouts = FXCollections.observableArrayList();



    public DAO() throws IOException {
        emf = Persistence.createEntityManagerFactory("tp");
        em = emf.createEntityManager();

        try (FileInputStream in = new FileInputStream("src\\main\\resources\\config.properties")) {
            properties = new Properties();
            properties.load(in);
        }

        //importData();
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

    public List<Session> getSessions() {
        TypedQuery<Session> query = em.createQuery("SELECT s FROM Session s ORDER BY s.dateTime DESC", Session.class);
        return query.getResultList();
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

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) throws IOException {
        try (FileOutputStream out = new FileOutputStream("src\\main\\resources\\config.properties")){
            properties.setProperty(key, value);
            properties.store(out, null);
        }
    }

    public Workout findLastWorkout(Exercise exercise) {
        for (Session session : getSessions()) {
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
        for (Session session : getSessions()) {
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

    public long findEstimatedDuration(){
        long estimatedDuration = 0;
        for (Exercise exercise : sessionExercises) {
            estimatedDuration += findAverageExerciseDuration(exercise);
        }
        return estimatedDuration*1000;
    }

    public String averageDurationString(Exercise exercise){
        long duration = findAverageExerciseDuration(exercise);
        return String.format("%02d", duration / 60) + ":" + String.format("%02d", duration % 60);
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
            return 600;
        }
    }

    public void close() {
        em.close();
        emf.close();
    }




    // TODO tee ilusaks - eraldi meetodid begin, commit ja need siis fromFile meetodisse ja sealt ikka oleks sisse lugemine

    public void importData() throws IOException{
        for (Exercise exercise : FromFile.importExercises("src\\main\\resources\\org\\example\\tp\\data\\exercises.txt")) {
            em.getTransaction().begin();
            em.persist(exercise);
            em.getTransaction().commit();
        }

        File file = new File("src\\main\\resources\\org\\example\\tp\\data\\history.txt");

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm");
        String line;

        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            String[] sessionData = data[0].split(";");
            LocalDateTime dateTime = LocalDateTime.parse(sessionData[2], dateFormatter);
            Session session = new Session(Integer.parseInt(sessionData[0]),
                    sessionData[1].replaceAll("¤", ","),
                    dateTime,
                    Integer.parseInt(sessionData[3]));
            if (sessionData.length > 4) {
                session.setComment(sessionData[4].replaceAll("¤", ","));
            }
            LocalDate date = dateTime.toLocalDate();
            em.getTransaction().begin();
            for (int i = 1; i < data.length; i++) {
                String[] workoutData = data[i].split(";");

                Workout workout = new Workout(getExerciseById(Integer.parseInt(workoutData[0])),
                        workoutData[1],
                        workoutData[2].isEmpty() ? 0 : Float.parseFloat(workoutData[2]),
                        Integer.parseInt(workoutData[3]),
                        date);

                if (workoutData.length > 4) {
                    workout.setComment(workoutData[4].replaceAll("¤", ","));
                }

                em.persist(workout);
                session.addWorkout(workout);
            }

            em.persist(session);

            em.getTransaction().commit();
        }
    }
}




