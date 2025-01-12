package org.example.tp.controllers;

import javafx.scene.Node;
import javafx.util.Duration;
import org.example.tp.dao.DAO;
import org.example.tp.dataobjects.Exercise;
import org.example.tp.dataobjects.Workout;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static org.example.tp.dataobjects.Exercise.Category.*;
import static org.example.tp.logic.Tab.*;

// TODO reorder with drag and drop
// TODO auto sort (less weight change, shuffle types, priority - preffered ex. in the beginning)

// TODO order exercises by how frequently they have been on workouts
// TODO history calendar view and tabular view
// TODO history - redo old workout, dropdown menu above current workout to autofill current workout

// TODO show time and duration someway better (progress bar and times on the bar and at the end)
// TODO save time with millis everywhere, just show in seconds

// TODO add logging
// TODO combine exercises and workout tab


// later
// TODO menubar, add toggle to change rep box update method (previous values from this or last exercise)
// TODO menubar, add a button to add exercises to database
// TODO window size change
// TODO review the weight-rep relation graph
// TODO more exercises

// TODO files are for import and export

/*
DONE
* database
* refresh workout tab each time (when chancing exercises workout doesn't update immidiately)
* avg duration instead of last time duration
* current workout delete with double click
* better logo
* autofill repetitions with TAB
* remainders (supplements at the beginning and stop exercise on the watch at the end)
* dumbell calculator / pics of dumbell with highlighted weights
* focus always on repetition box when changing workouts (last visible)
* all workout exercises displayed

* 22.5 dumbell image
* dumbell images transparent background
* fill repetition boxes with previous value (first one with info from last workout)
* repetitions graph show change better
* when hoover on graph then show exact values
* menubar, add option to turn remainders off and on (and change text)
 */


public class ExerciseTab implements Initializable {
    private final DAO dao;

    private final XYChart.Series<String, Number> weightSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> repSeries = new XYChart.Series<>();

    private Exercise currentExercise;

    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private Label exerciseName;
    @FXML
    private Label notesHeaderLable;
    @FXML
    private Label weightLabel;
    @FXML
    private Label repetitionsLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label notesLabel;
    @FXML
    private ListView<Exercise> absList;
    @FXML
    private ListView<Exercise> backList;
    @FXML
    private ListView<Exercise> bicepsList;
    @FXML
    private ListView<Exercise> chestList;
    @FXML
    private ListView<Exercise> shouldersList;
    @FXML
    private ListView<Exercise> tricepsList;
    @FXML
    private ListView<Exercise> legsList;
    @FXML
    private ListView<Exercise> selectedExercisesList;
    @FXML
    private ImageView exerciseImage;
    @FXML
    private ImageView dumbellImage;
    @FXML
    private Button addToWorkoutButton;
    @FXML
    private Button removeFromWorkoutButton;
    @FXML
    private Button upButton;
    @FXML
    private Button downButton;

    @FXML
    public void absClicked() {
        updateExerciseInfo(absList);
    }

    @FXML
    public void backClicked() {
        updateExerciseInfo(backList);
    }

    @FXML
    public void bicepsClicked() {
        updateExerciseInfo(bicepsList);
    }

    @FXML
    public void chestClicked() {
        updateExerciseInfo(chestList);
    }

    @FXML
    public void shouldersClicked() {
        updateExerciseInfo(shouldersList);
    }

    @FXML
    public void tricepsClicked() {
        updateExerciseInfo(tricepsList);
    }

    @FXML
    public void legsClicked() {
        updateExerciseInfo(legsList);
    }

    @FXML
    public void selectedExercisesClicked() {
        updateExerciseInfo(selectedExercisesList);
    }

    @FXML
    public void addToSessionClicked() {
        addNewExercise();
    }

    @FXML
    public void removeFromSessionClicked() {
        removeExercise();
    }

    @FXML
    public void upButtonClicked() {
        changeOrder("up");
    }

    @FXML
    public void downButtonClicked() {
        changeOrder("down");
    }

    public ExerciseTab(DAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadExercises();
        loadGraphData();

        List<ListView<Exercise>> allExercises = Arrays.asList(absList, backList, bicepsList, chestList, shouldersList, tricepsList, legsList);

        for (ListView<Exercise> listView : allExercises) {
            listView.setOnMouseClicked(event -> {
                updateExerciseInfo(listView);
                if (event.getClickCount() == 2)
                    addNewExercise();
            });
        }

        selectedExercisesList.setOnMouseClicked(event -> {
        updateExerciseInfo(selectedExercisesList);
        if (event.getClickCount() == 2)
            removeExercise();
        });

    }

    private void loadExercises() {
        cellFactories(getExercisesList(ABS), absList);
        cellFactories(getExercisesList(BACK), backList);
        cellFactories(getExercisesList(BICEPS), bicepsList);
        cellFactories(getExercisesList(CHEST), chestList);
        cellFactories(getExercisesList(SHOULDERS), shouldersList);
        cellFactories(getExercisesList(TRICEPS), tricepsList);
        cellFactories(getExercisesList(LEGS), legsList);
    }

    private void loadGraphData() {
        weightSeries.setName("Weights");
        repSeries.setName("Repetitions");
        lineChart.setAnimated(false);
    }

    private void updateGraph(Exercise exercise) {
        if (!lineChart.getData().isEmpty()) {
            clearGraph();
        }

        List<Workout> workouts = dao.findAllWorkouts(exercise);

        if (!workouts.isEmpty()) {
            lineChart.getData().add(weightSeries);
            lineChart.getData().add(repSeries);

            workouts = workouts.subList(0, Math.min(100, workouts.size()));
            Collections.reverse(workouts);

            for (Workout workout : workouts) {
                float weight = workout.getWeight();
                double reps = Arrays.stream(workout.getRepetitions()).average().orElse(0);
                String date = workout.getDateString();

                Tooltip tooltip = new Tooltip(date + "\n  Reps: " + reps);
                tooltip.setShowDelay(Duration.millis(0));

                if (weight != 0){
                    tooltip.setText(tooltip.getText() + "\n  Weight: " + weight);

                    Data<String, Number> weight_data = new Data<>(date, weight);
                    weightSeries.getData().add(weight_data);
                    Tooltip.install(weight_data.getNode(), tooltip);
                }

                Data<String, Number> reps_data = new Data<>(date, reps);
                repSeries.getData().add(reps_data);
                Tooltip.install(reps_data.getNode(), tooltip);
            }
        }
    }

    private void clearGraph() {
        lineChart.getData().clear();

        weightSeries.getData().clear();
        repSeries.getData().clear();
    }

    public void updateTab() {
        cellFactoriesWithCategory(dao.getSessionExercises(), selectedExercisesList);
    }

    private void changeOrder(String direction) {
        ObservableList<Exercise> sessionExercises = dao.getSessionExercises();
        ObservableList<Workout> sessionWorkouts = dao.getSessionWorkouts();

        int index = sessionExercises.indexOf(currentExercise);

        if (index != -1) {
            Exercise selectedExercise = sessionExercises.remove(index);
            Workout selectedWorkout = sessionWorkouts.remove(index);

            if (direction.equals("up")) {
                index = Math.max(index - 1, 0);
            } else if (direction.equals("down")) {
                index = Math.min(index + 1, sessionExercises.size());
            }
            sessionExercises.add(index, selectedExercise);
            sessionWorkouts.add(index, selectedWorkout);

            dao.setSessionExercises(sessionExercises);
            cellFactoriesWithCategory(sessionExercises, selectedExercisesList);

            selectedExercisesList.getSelectionModel().select(index);
        }
    }

    private void addNewExercise() {
        ObservableList<Exercise> sessionExercises = dao.getSessionExercises();
        ObservableList<Workout> sessionWorkouts = dao.getSessionWorkouts();
        if (!sessionExercises.contains(currentExercise)) {
            sessionExercises.add(currentExercise);

            Workout lastWorkout = dao.findLastWorkout(currentExercise);
            float weight = lastWorkout != null ? lastWorkout.getWeight() : 0;
            sessionWorkouts.add(new Workout(currentExercise, LocalDate.now(), weight));

            dao.setSessionExercises(sessionExercises);
            dao.setSessionWorkouts(sessionWorkouts);
        }

        cellFactoriesWithCategory(sessionExercises, selectedExercisesList);

        selectedExercisesList.getSelectionModel().select(currentExercise);
        updateExerciseInfo(selectedExercisesList);
    }

    private void removeExercise() {
        ObservableList<Exercise> sessionExercises = dao.getSessionExercises();
        if (sessionExercises.contains(currentExercise)) {
            sessionExercises.remove(currentExercise);
            dao.setSessionExercises(sessionExercises);
            dao.removeFromSessionWorkouts(currentExercise);
        }

        cellFactoriesWithCategory(sessionExercises, selectedExercisesList);

        selectedExercisesList.getSelectionModel().select(currentExercise);
        updateExerciseInfo(selectedExercisesList);
    }

    private void updateExerciseInfo(ListView<Exercise> exerciseListView) {
        if (exerciseListView.getFocusModel().getFocusedItem() != null) {
            currentExercise = exerciseListView.getFocusModel().getFocusedItem();
        }
        Workout lastWorkout = dao.findLastWorkout(currentExercise);

        exerciseName.setText(currentExercise.getName());
        categoryLabel.setText(currentExercise.getCategory().name());

        if (lastWorkout != null) {
            weightLabel.setText(Float.toString(lastWorkout.getWeight()));
            repetitionsLabel.setText(lastWorkout.getRepetitionsString(" - "));
            durationLabel.setText(dao.averageDurationString(currentExercise));

            if (lastWorkout.getComment() != null) {
                notesHeaderLable.setText("Notes");
                notesLabel.setText(lastWorkout.getComment());
            } else {
                notesHeaderLable.setText("");
                notesLabel.setText("");
            }

            dumbellImage.setImage(getDumbellImageByWeight(lastWorkout.getWeight()));

        } else {
            weightLabel.setText("NaN");
            repetitionsLabel.setText("NaN");
            durationLabel.setText("NaN");
            notesHeaderLable.setText("");
            notesLabel.setText("");
            dumbellImage.setImage(null);
        }

        exerciseImage.setImage(currentExercise.getImage());
        exerciseImage.setPreserveRatio(true);
        exerciseImage.setFitWidth(360);

        updateGraph(currentExercise);

        boolean isInCurrentSession = dao.getSessionExercises().contains(currentExercise);

        upButton.setDisable(!isInCurrentSession);
        downButton.setDisable(!isInCurrentSession);
        addToWorkoutButton.setDisable(isInCurrentSession);
        removeFromWorkoutButton.setDisable(!isInCurrentSession);
    }

    private ObservableList<Exercise> getExercisesList(Exercise.Category category) {
        List<Exercise> allExercises = dao.getExercises();
        ObservableList<Exercise> selectedExercises = FXCollections.observableArrayList();

        for (Exercise exercise : allExercises) {
            if (exercise.getCategory() == category) {
                selectedExercises.add(exercise);
            }
        }

        return selectedExercises;
    }
}
