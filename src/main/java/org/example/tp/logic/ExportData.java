package org.example.tp.logic;

import com.google.gson.*;
import org.example.tp.dao.DAO;
import org.example.tp.dataobjects.Session;
import org.example.tp.dataobjects.Workout;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

public class ExportData {

    public static void exercisesToJson(File file, DAO dao) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))){
            bw.write(gson.toJson(dao.getExercises()));
        }
    }

    public static void historyToJson(File file, DAO dao) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonArray sessions = new JsonArray();

        for (Session session : dao.getAllSessions()) {
            JsonObject sessionData = new JsonObject();
            sessionData.addProperty("id", session.getId());
            sessionData.addProperty("name", session.getName());
            sessionData.addProperty("dateTime", session.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:nnnnnn")));
            sessionData.addProperty("duration", session.getDuration());
            sessionData.addProperty("comment", session.getComment());

            JsonArray workouts = new JsonArray();

            for (Workout workout : session.getWorkouts()) {
                JsonObject workoutData = new JsonObject();
                workoutData.addProperty("id", workout.getId());
                workoutData.addProperty("exerciseId", workout.getExercise().getId());
                workoutData.addProperty("weight", workout.getWeight());
                workoutData.addProperty("repetitions", workout.getRepetitionsString("-"));
                workoutData.addProperty("duration", workout.getDuration());
                workoutData.addProperty("comment", workout.getComment());

                workouts.add(workoutData);
            }
            sessionData.add("workouts", workouts);
            sessions.add(sessionData);
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))){
            bw.write(gson.toJson(sessions));
        }

    }
}
