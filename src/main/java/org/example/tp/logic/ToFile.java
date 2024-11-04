package org.example.tp.logic;

import org.example.tp.dataobjects.Session;
import org.example.tp.dataobjects.Workout;

import java.io.*;
import java.time.format.DateTimeFormatter;

public class ToFile {
    public static void addSession(String pathName, Session session) throws IOException {
        File file = new File(pathName);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
        StringBuilder sessionData = new StringBuilder();

        sessionData.append(session.getId()).append(";");
        sessionData.append(session.getName().replaceAll(",", "¤")).append(";");
        sessionData.append(session.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm"))).append(";");
        sessionData.append(session.getDuration());
        if (session.getComment() != null) {
            sessionData.append(";").append(session.getComment().replaceAll(",", "¤"));
        }


        for (Workout workout : session.getWorkouts()) {
            sessionData.append(",");
            sessionData.append(workout.getExercise().getId()).append(";");
            sessionData.append(workout.getRepetitionsString("-")).append(";");
            sessionData.append(workout.getWeight() == 0 ? "" : workout.getWeight()).append(";");
            sessionData.append(workout.getDuration());
            if (workout.getComment() != null) {
                sessionData.append(";").append(workout.getComment().replaceAll(",", "¤"));
            }
        }

        bw.write(sessionData.toString());
        bw.newLine();

        bw.close();
    }
}
