package org.example.tp.dataobjects;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Entity
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;
    private LocalDate date;
    private float weight;
    private String repetitions;
    private long duration;
    private String comment;


    public Workout(Exercise exercise, String repetitions, float weight, int duration, LocalDate date) {
        this.exercise = exercise;
        this.repetitions = repetitions;
        this.weight = weight;
        this.duration = duration;
        this.date = date;
    }

    public Workout(Exercise exercise, LocalDate date, float weight) {
        this.exercise = exercise;
        this.date = date;
        this.weight = weight;
    }

    public Workout() {
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setRepetitions(int[] repetitions) {
        this.repetitions = Arrays.toString(repetitions)
                .replace(", ", "-")
                .replace("[", "")
                .replace("]", "");
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public int[] getRepetitions() {
        if (repetitions == null || repetitions.isEmpty()) {
            return null;
        }
        return Arrays.stream(repetitions.split("-")).mapToInt(Integer::parseInt).toArray();
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
        return repetitions.replace("-", separator);
    }

    @Override
    public String toString() {
        return "Workout{" +
                "exercise=" + exercise +
                ", repetitions=" + repetitions +
                ", weight=" + weight +
                ", duration=" + duration +
                '}';
    }
}
