package org.example.tp.logic;

import com.google.gson.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.example.tp.dao.DAO;
import org.example.tp.dataobjects.Exercise;
import org.example.tp.dataobjects.Session;
import org.example.tp.dataobjects.Workout;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.example.tp.logic.Tab.newAlert;

public class ImportData {

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

        Alert alert = newAlert("Warning!", "Overwriting excisting data!", "You are overwrighting all exercises.\nAre you sure you want to continue?", Alert.AlertType.CONFIRMATION);
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> dao.saveExercises(exercises));
    }

    public static void historyFromJson(File file, DAO dao) throws IOException {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:nnnnnn");
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
            String comment = sessionObject.has("comment") ? sessionObject.get("comment").getAsString() : null;
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
                String workoutComment = workoutObject.has("comment") ? workoutObject.get("comment").getAsString() : null;

                session.addWorkout(new Workout(workoutId, exercise, date, weight, repetitions, workoutDuration, workoutComment));
            }
            sessions.add(session);
        }

        Alert alert = newAlert("Warning!", "Overwriting existing data!", "You are overwriting all history.\nAre you sure you want to continue?", Alert.AlertType.CONFIRMATION);

        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    dao.saveSessions(sessions);
                });
    }
}
