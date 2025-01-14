package org.example.tp.logic;

import com.google.gson.*;
import org.example.tp.dao.DAO;
import org.example.tp.dataobjects.Exercise;
import org.example.tp.dataobjects.Session;
import org.example.tp.dataobjects.Workout;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ImportData {

    public static void historyFromJson(File file, DAO dao) throws IOException {
        Gson gson = new GsonBuilder().create();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        JsonElement historyData;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
            historyData = JsonParser.parseReader(br);
        }

        ArrayList<Session> sessions = new ArrayList<>();

        for (JsonElement sessionJson : historyData.getAsJsonArray()) {
            JsonObject sessionObject = sessionJson.getAsJsonObject();

            int id = sessionObject.get("id").getAsInt();
            String name = sessionObject.get("name").getAsString();
            LocalDateTime dateTime = LocalDateTime.parse(sessionObject.get("dateTime").getAsString(), dateFormatter);
            long duration = sessionObject.get("duration").getAsLong();
            String comment = sessionObject.get("comment") != null ? sessionObject.get("comment").getAsString() : null;
            JsonElement workoutsJson = sessionObject.get("workouts");

            Session session = new Session(id, name, dateTime, duration, comment);

            for (JsonElement workoutJson : workoutsJson.getAsJsonArray()) {
                JsonObject workoutObject = workoutJson.getAsJsonObject();

                long workoutId = workoutObject.get("id").getAsLong();
                Exercise exercise = dao.getExerciseById(workoutObject.get("exerciseId").getAsInt());
                LocalDate date = dateTime.toLocalDate();
                float weight = workoutObject.get("weight").getAsFloat();
                String repetitions = workoutObject.get("repetitions").getAsString();
                long workoutDuration = workoutObject.get("duration").getAsLong();
                String workoutComment = workoutObject.get("comment") != null ? workoutObject.get("comment").getAsString() : null;

                session.addWorkout(new Workout(workoutId, exercise, date, weight, repetitions, workoutDuration, workoutComment));
            }
            sessions.add(session);
        }

        // TODO add to database
        for (Session session : sessions) {
            System.out.println(session);
        }
    }

    public static void exercisesFromJson(File file, DAO dao) throws IOException {
        Gson gson = new GsonBuilder().create();
        JsonElement exercisesData;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
            exercisesData = JsonParser.parseReader(br);
        }

        JsonArray exercisesJson = exercisesData.getAsJsonArray();
        ArrayList<Exercise> exercises = new ArrayList<>();

        for (JsonElement json : exercisesJson) {
            exercises.add(gson.fromJson(json, Exercise.class));
        }

        // TODO add to database
        for (Exercise exercise : exercises) {
            System.out.println(exercise);
        }
    }
}
