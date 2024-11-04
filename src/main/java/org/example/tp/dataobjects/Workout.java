package org.example.tp.dataobjects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Workout {
    private final Exercise exercise;
    private final LocalDate date;
    private float weight;
    private int[] repetitions;
    private long duration;
    private String comment;

    public Workout(Exercise exercise, int[] repetitions, float weight, int duration, LocalDate date) {
        this.exercise = exercise;
        this.repetitions = repetitions;
        this.weight = weight;
        this.duration = duration;
        this.date = date;
    }

    public Workout(Exercise exercise, LocalDate date) {
        this.exercise = exercise;
        this.date = date;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setRepetitions(int[] repetitions) {
        this.repetitions = repetitions;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public int[] getRepetitions() {
        return repetitions;
    }

    public float getWeight() {
        return weight;
    }

    public long getDuration() {
        return duration;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }


    public String getDurationString() {
        return String.format("%02d", duration / 60) + ":" + String.format("%02d", duration % 60);
    }

    public String getDateString() {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getRepetitionsString(String separator) {
        return Arrays.toString(repetitions)
                .replace(", ", separator)
                .replace("[", "")
                .replace("]", "");
    }

    @Override
    public String toString() {
        return "Workout{" +
                "exercise=" + exercise +
                ", repetitions=" + Arrays.toString(repetitions) +
                ", weight=" + weight +
                ", duration=" + duration +
                '}';
    }
}
