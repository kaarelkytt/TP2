package org.example.tp.logic;

import org.example.tp.dao.DAO;
import org.example.tp.dataobjects.Exercise;
import org.example.tp.dataobjects.Session;
import org.example.tp.dataobjects.Workout;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FromFile {
    public static List<Session> importHistory(String pathName, DAO dao) throws IOException {
        List<Session> history = new ArrayList<>();
        File file = new File(pathName);

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

            for (int i = 1; i < data.length; i++) {
                String[] workoutData = data[i].split(";");

                Workout workout = new Workout(dao.getExerciseById(Integer.parseInt(workoutData[0])),
                        Arrays.stream(workoutData[1].split("-")).mapToInt(Integer::parseInt).toArray(),
                        workoutData[2].isEmpty() ? 0 : Float.parseFloat(workoutData[2]),
                        Integer.parseInt(workoutData[3]),
                        date);

                if (workoutData.length > 4) {
                    workout.setComment(workoutData[4].replaceAll("¤", ","));
                }
                session.addWorkout(workout);
            }

            history.add(0, session);
        }

        return history;
    }

    public static List<Exercise> importExercises(String pathName) throws IOException {
        List<Exercise> Exercises = new ArrayList<>();
        File file = new File(pathName);

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;

        while ((line = br.readLine()) != null) {
            String[] data = line.split(";");
            Exercises.add(new Exercise(Integer.parseInt(data[0]), data[1], data[2], data[3]));
        }

        return Exercises;
    }
}
