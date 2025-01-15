package org.example.tp.controllers;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.tp.dao.DAO;
import org.example.tp.dataobjects.Exercise;
import org.example.tp.dataobjects.Session;
import org.example.tp.dataobjects.Workout;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static org.example.tp.dataobjects.Exercise.Category.*;
import static org.example.tp.logic.Tab.*;


// TODO reorder with drag and drop
// TODO auto sort (less weight change, shuffle types, priority - preffered ex. in the beginning)
// TODO menubar close

// TODO order exercises by how frequently they have been on workouts
// TODO history calendar view and tabular view
// TODO history - redo old workout, dropdown menu above current workout to autofill current workout

// TODO bigger texts


// later
// TODO menubar, add toggle to change rep box update method (previous values from this or last exercise)
// TODO menubar, add a button to add exercises to database
// TODO window size change
// TODO more exercises
// TODO add logging
// TODO refresh after importing


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
* combine exercises and workout tab
* show time and duration someway better (progress bar and times on the bar and at the end)

* methods for import and export
* save time with millis everywhere, just show in seconds

* clean after session end
* workout ids in correct order

* exercise categories color
 */


public class NewWorkoutTab implements Initializable {
    private final DAO dao;
    private Session newSession;
    private Workout currentWorkout;

    private boolean running = false;
    private long startTimeMillis = 0;
    private long exerciseStartTimeMillis;
    private AnimationTimer animationTimer;
    private TextField[] reps;

    private final XYChart.Series<String, Number> weightSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> repSeries = new XYChart.Series<>();

    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private Label exerciseName;
    @FXML
    private Label weightLabel;
    @FXML
    private Label weightNowLabel;
    @FXML
    private Label repetitionsLabel;
    @FXML
    private Label repetitionsNowLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label durationNowLabel;
    @FXML
    private Label currentExerciseDurationLabel;
    @FXML
    private Label elapsedTimeLabel;
    @FXML
    private Label remainedTimeLabel;
    @FXML
    private Label startTimeLabel;
    @FXML
    private Label estimatedDurationLabel;
    @FXML
    private Label estimatedEndLabel;

    @FXML
    private TextField repsTextField1;
    @FXML
    private TextField repsTextField2;
    @FXML
    private TextField repsTextField3;
    @FXML
    private TextField repsTextField4;
    @FXML
    private TextField repsTextField5;

    @FXML
    private TextField weightTextField;
    @FXML
    private TextArea commentTextArea;

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
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button finishButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button upButton;
    @FXML
    private Button downButton;
    @FXML
    private Button addToWorkoutButton;
    @FXML
    private Button removeFromWorkoutButton;

    @FXML
    private ImageView exerciseImage;
    @FXML
    private ImageView dumbellImage;

    @FXML
    private ListView<Workout> sessionWorkoutsList;

    @FXML
    private ProgressBar progressBar;

    @FXML
    public void startButtonClicked() throws IOException {
        startSession();
    }
    @FXML
    public void finishButtonClicked() throws IOException {
        saveSession();
    }
    @FXML
    public void pauseButtonClicked() {
        pauseWorkout();
    }
    @FXML
    public void nextButtonClicked() {
        nextExercise();
    }
    @FXML
    public void previousButtonClicked() {
        previousExercise();
    }
    @FXML
    public void upButtonClicked() {
        changeOrder("up");
    }
    @FXML
    public void downButtonClicked() {
        changeOrder("down");
    }
    @FXML
    public void addToSessionClicked() {
        addNewWorkout();
    }
    @FXML
    public void removeFromSessionClicked() {
        removeWorkout();
    }

    @FXML
    public void absClicked() {
        updateCurrentWorkoutFromExercises(absList);
    }
    @FXML
    public void backClicked() {
        updateCurrentWorkoutFromExercises(backList);
    }
    @FXML
    public void bicepsClicked() {
        updateCurrentWorkoutFromExercises(bicepsList);
    }
    @FXML
    public void chestClicked() {
        updateCurrentWorkoutFromExercises(chestList);
    }
    @FXML
    public void shouldersClicked() {
        updateCurrentWorkoutFromExercises(shouldersList);
    }
    @FXML
    public void tricepsClicked() {
        updateCurrentWorkoutFromExercises(tricepsList);
    }
    @FXML
    public void legsClicked() {
        updateCurrentWorkoutFromExercises(legsList);
    }

    @FXML
    public void repetitionBoxKeyTyped() {
        updateRepetitionBoxPromptText();
    }
    @FXML
    public void repetitionBoxUpdate() {
        updateRepetitionBox();
    }
    @FXML
    public void weightUpdate() {
        updateDumbellImage();
    }


    public NewWorkoutTab(DAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reps = new TextField[]{repsTextField1, repsTextField2, repsTextField3, repsTextField4, repsTextField5};
        cellFactoriesWithImages(dao.getSessionWorkouts(), sessionWorkoutsList);

        loadExercises();
        loadGraphData();

        List<ListView<Exercise>> allExercises = Arrays.asList(absList, backList, bicepsList, chestList, shouldersList, tricepsList, legsList);

        for (ListView<Exercise> listView : allExercises) {
            listView.setOnMouseClicked(event -> {
                updateCurrentWorkoutFromExercises(listView);
                if (event.getClickCount() == 2)
                    addNewWorkout();
            });
        }

        sessionWorkoutsList.setOnMouseClicked(event -> {
            updateCurrentWorkoutFromSelected();
            if (event.getClickCount() == 2)
                removeWorkout();
        });

        exerciseImage.setPreserveRatio(true);
        exerciseImage.setFitWidth(360);
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

    private void loadGraphData() {
        weightSeries.setName("Weights");
        repSeries.setName("Repetitions");
        lineChart.setAnimated(false);
    }

    private void updateGraph() {
        if (!lineChart.getData().isEmpty()) {
            clearGraph();
        }

        List<Workout> workouts = dao.findAllWorkouts(currentWorkout.getExercise());

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

                if (weight != 0) {
                    tooltip.setText(tooltip.getText() + "\n  Weight: " + weight);

                    XYChart.Data<String, Number> weight_data = new XYChart.Data<>(date, weight);
                    weightSeries.getData().add(weight_data);
                    Tooltip.install(weight_data.getNode(), tooltip);
                }

                XYChart.Data<String, Number> reps_data = new XYChart.Data<>(date, reps);
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

    private void changeOrder(String direction) {
        ObservableList<Workout> sessionWorkouts = dao.getSessionWorkouts();

        int index = sessionWorkouts.indexOf(currentWorkout);

        if (index != -1) {
            Workout selectedWorkout = sessionWorkouts.remove(index);

            if (direction.equals("up")) {
                index = Math.max(index - 1, 0);
            } else if (direction.equals("down")) {
                index = Math.min(index + 1, sessionWorkouts.size());
            }
            sessionWorkouts.add(index, selectedWorkout);

            sessionWorkoutsList.getSelectionModel().select(index);
        }
    }

    private void addNewWorkout() {
        ObservableList<Workout> sessionWorkouts = dao.getSessionWorkouts();
        if (!dao.isAddedToSessionWorkouts(currentWorkout.getExercise())) {
            sessionWorkouts.add(currentWorkout);
            dao.setSessionWorkouts(sessionWorkouts);
        }

        sessionWorkoutsList.getSelectionModel().select(currentWorkout);
        updateWorkoutInfo();
    }

    private void removeWorkout() {
        ObservableList<Workout> sessionWorkouts = dao.getSessionWorkouts();
        if (dao.isAddedToSessionWorkouts(currentWorkout.getExercise())) {
            sessionWorkouts.remove(currentWorkout);
            dao.setSessionWorkouts(sessionWorkouts);
        }

        updateCurrentWorkoutFromSelected();
        updateWorkoutInfo();
    }

    private void clearExerciseInfo() {
        exerciseName.setText("");
        weightTextField.setText("");
        weightLabel.setText("");
        repetitionsLabel.setText("");
        repsTextField1.setText("");
        repsTextField2.setText("");
        repsTextField3.setText("");
        repsTextField4.setText("");
        repsTextField5.setText("");
        repsTextField1.setPromptText("");
        repsTextField2.setPromptText("");
        repsTextField3.setPromptText("");
        repsTextField4.setPromptText("");
        repsTextField5.setPromptText("");
        durationLabel.setText("");
        currentExerciseDurationLabel.setText("00:00:00");
        commentTextArea.setPromptText("");
        commentTextArea.setText("");
        elapsedTimeLabel.setText("00:00:00");
        startTimeLabel.setText("00:00");
        remainedTimeLabel.setText("00:00:00");
        estimatedEndLabel.setText("00:00");
        estimatedDurationLabel.setText("00:00:00");
        progressBar.setProgress(0);
        exerciseImage.setImage(null);
        dumbellImage.setImage(null);
        clearGraph();
        updateRepetitionBox();
    }

    private void updateCurrentWorkoutFromExercises(ListView<Exercise> exerciseListView) {
        saveCurrentWorkout();

        if (exerciseListView.getFocusModel().getFocusedItem() != null) {
            Exercise focusedExercise = exerciseListView.getFocusModel().getFocusedItem();

            if (dao.isAddedToSessionWorkouts(focusedExercise)) {
                currentWorkout = dao.getSessionWorkout(focusedExercise);
                sessionWorkoutsList.getSelectionModel().select(currentWorkout);
            } else {
                Workout lastWorkout = dao.findLastWorkout(focusedExercise);
                float weight = lastWorkout != null ? lastWorkout.getWeight() : 0;
                currentWorkout = new Workout(focusedExercise, LocalDate.now(), weight);
                sessionWorkoutsList.getSelectionModel().clearSelection();
            }
        }

        updateWorkoutInfo();
    }

    private void updateCurrentWorkoutFromSelected() {
        saveCurrentWorkout();

        if (sessionWorkoutsList.getFocusModel().getFocusedItem() != null) {
            currentWorkout = sessionWorkoutsList.getFocusModel().getFocusedItem();
        }

        updateWorkoutInfo();
    }


    private void updateWorkoutInfo() {
        if (currentWorkout == null) {
            clearExerciseInfo();
            return;
        }

        Exercise currentExercise = currentWorkout.getExercise();
        Workout lastWorkout = dao.findLastWorkout(currentExercise);
        int[] lastReps = null;

        exerciseName.setText(currentExercise.getName() + " (" + currentExercise.getCategory().name() + ")");

        if (lastWorkout != null) {
            weightLabel.setText(Float.toString(lastWorkout.getWeight()));
            weightTextField.setText(Float.toString(lastWorkout.getWeight()));
            repetitionsLabel.setText(lastWorkout.getRepetitionsString(" - "));
            durationLabel.setText(dao.averageDurationString(currentExercise));
            commentTextArea.setPromptText(lastWorkout.getComment());
            lastReps = lastWorkout.getRepetitions();
        } else {
            weightLabel.setText("NaN");
            weightTextField.setText("");
            repetitionsLabel.setText("NaN");
            durationLabel.setText("NaN");
            commentTextArea.setPromptText(null);
        }

        int[] currentReps = currentWorkout.getRepetitions();

        for (int i = 0; i < reps.length; i++) {
            if (currentReps != null && i < currentReps.length) {
                reps[i].setText(String.valueOf(currentReps[i]));
            } else {
                reps[i].setText("");
            }

            if (lastReps != null && i < lastReps.length) {
                reps[i].setPromptText(String.valueOf(lastReps[i]));
            } else {
                reps[i].setPromptText("");
            }
        }
        updateRepetitionBox();

        if (currentWorkout.getWeight() != 0) {
            weightTextField.setText(String.valueOf(currentWorkout.getWeight()));
        }

        if (currentWorkout.getDuration() != 0) {
            exerciseStartTimeMillis = System.currentTimeMillis() - currentWorkout.getDuration();
            currentExerciseDurationLabel.setText(getFormattedTimeFromMillis(currentWorkout.getDuration(), false));
        } else {
            exerciseStartTimeMillis = System.currentTimeMillis();
            currentExerciseDurationLabel.setText("00:00:00");
        }

        estimatedDurationLabel.setText(getFormattedTimeFromMillis(dao.findEstimatedDuration(), false));
        commentTextArea.setText(currentWorkout.getComment());
        exerciseImage.setImage(currentExercise.getImage());

        updateGraph();
        updateDumbellImage();
        updateRepetitionBoxFocus();
        updateButtons();
        updateTextFields();

        boolean isInCurrentSession = dao.isAddedToSessionWorkouts(currentExercise);
        upButton.setDisable(!isInCurrentSession);
        downButton.setDisable(!isInCurrentSession);
        addToWorkoutButton.setDisable(isInCurrentSession);
        removeFromWorkoutButton.setDisable(!isInCurrentSession);
    }

    private void updateTextFields() {
        boolean isInCurrentSession = currentWorkout != null && dao.isAddedToSessionWorkouts(currentWorkout.getExercise());

        weightNowLabel.setVisible(isInCurrentSession);
        repetitionsNowLabel.setVisible(isInCurrentSession);
        durationNowLabel.setVisible(isInCurrentSession);
        weightTextField.setVisible(isInCurrentSession);
        repsTextField1.setVisible(isInCurrentSession);
        currentExerciseDurationLabel.setVisible(isInCurrentSession);

        commentTextArea.setDisable(!isInCurrentSession);
    }


    private void updateRepetitionBox() {
        repsTextField2.setVisible(!repsTextField1.getText().isEmpty());
        repsTextField3.setVisible(!repsTextField2.getText().isEmpty());
        repsTextField4.setVisible(!repsTextField3.getText().isEmpty());
        repsTextField5.setVisible(!repsTextField4.getText().isEmpty());
    }

    private void updateRepetitionBoxPromptText() {
        repsTextField2.setPromptText(repsTextField1.getText());
        repsTextField3.setPromptText(repsTextField2.getText());
        repsTextField4.setPromptText(repsTextField3.getText());
        repsTextField5.setPromptText(repsTextField4.getText());
    }

    private void updateRepetitionBoxFocus() {
        for (TextField rep : reps) {
            if (rep.isVisible()) {
                rep.requestFocus();
            }
        }
    }

    private void updateDumbellImage() {
        sessionWorkoutsList.refresh();

        try {
            if (!weightTextField.getText().isBlank()) {
                float weight = Float.parseFloat(weightTextField.getText());
                dumbellImage.setImage(getDumbellImageByWeight(weight));
                currentWorkout.setWeight(weight);
            } else {
                dumbellImage.setImage(null);
            }
        } catch (NumberFormatException e) {
            dumbellImage.setImage(null);
        }
    }


    private void startSession() throws IOException {
        if (dao.getProperty("startRemainder").equals("true")) {
            showStartAlert();
        }

        newSession = new Session(dao.getMaxSessionId() + 1, LocalDateTime.now());

        startTimeLabel.setText(newSession.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        estimatedEndLabel.setText(newSession.getDateTime().plusSeconds(dao.findEstimatedDuration()).format(DateTimeFormatter.ofPattern("HH:mm")));
        running = true;

        startTimeMillis = System.currentTimeMillis();
        exerciseStartTimeMillis = startTimeMillis;

        animationTimer = new AnimationTimer() {
            long pauseStart = 0;

            @Override
            public void handle(long now) {
                long estimatedDuration = dao.findEstimatedDuration();
                estimatedEndLabel.setText(newSession.getDateTime().plusSeconds(estimatedDuration / 1000).format(DateTimeFormatter.ofPattern("HH:mm")));

                if (running) {
                    if (pauseStart != 0) {
                        startTimeMillis += System.currentTimeMillis() - pauseStart;
                        pauseStart = 0;
                    }
                    long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
                    double progress = (double) elapsedMillis / estimatedDuration;

                    elapsedTimeLabel.setText(getFormattedTimeFromMillis(elapsedMillis, false));
                    remainedTimeLabel.setText(getFormattedTimeFromMillis(dao.findEstimatedDuration() - elapsedMillis, false));
                    currentExerciseDurationLabel.setText(getFormattedTimeFromMillis(System.currentTimeMillis() - exerciseStartTimeMillis, false));
                    progressBar.setProgress(progress);
                } else if (pauseStart == 0) {
                    pauseStart = System.currentTimeMillis();
                }
            }
        };
        animationTimer.start();

        pauseButton.setDisable(false);
        finishButton.setDisable(false);
        nextButton.setDisable(false);
        startButton.setDisable(true);
    }


    public void pauseWorkout() {
        if (startTimeMillis == 0) {
            return;
        }

        if (!saveCurrentWorkout()) {
            return;
        }

        if (!running) {
            updateWorkoutInfo();
        }

        running = !running;
        pauseButton.setText(running ? "Pause" : "Continue");
        exerciseImage.setImage(running ? currentWorkout.getExercise().getImage() : new Image("file:src/main/resources/org/example/tp/pics/pause.png"));
    }


    public void nextExercise() {
        sessionWorkoutsList.getSelectionModel().selectNext();
        updateCurrentWorkoutFromSelected();
        updateButtons();
    }


    public void previousExercise() {
        sessionWorkoutsList.getSelectionModel().selectPrevious();
        updateCurrentWorkoutFromSelected();
        updateButtons();
    }


    private void updateButtons() {
        int i = sessionWorkoutsList.getSelectionModel().getSelectedIndex();
        nextButton.setDisable(i >= sessionWorkoutsList.getItems().size() - 1);
        previousButton.setDisable(i <= 0);
    }


    public boolean saveCurrentWorkout() {
        boolean saved = true;

        if (currentWorkout == null) {
            return saved;
        }
        Workout workout = currentWorkout;

        try {
            if (!weightTextField.getText().isBlank()) {
                workout.setWeight(Float.parseFloat(weightTextField.getText()));
            }

            workout.setRepetitions(Arrays.stream(reps)
                    .filter(textField -> !textField.getText().isEmpty())
                    .mapToInt(textField -> Integer.parseInt(textField.getText()))
                    .toArray());

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.NONE, "Check your inputs!", ButtonType.OK);
            alert.setTitle("Invalid argument");
            alert.showAndWait();
            saved = false;
        }


        if (running) {
            workout.setDuration((System.currentTimeMillis() - exerciseStartTimeMillis));
        }

        workout.setComment(commentTextArea.getText());
        return saved;
    }

    private void saveSession() throws IOException {
        running = true;
        if (!saveCurrentWorkout()) {
            return;
        }
        newSession.setDuration((System.currentTimeMillis() - startTimeMillis));
        running = false;
        exerciseImage.setImage(new Image("file:src/main/resources/org/example/tp/pics/pause.png"));

        if (dao.getProperty("endRemainder").equals("true")) {
            showFinishAlert();
        }


        if (!showSummaryDialog()) {
            pauseWorkout();
            return;
        }

        Long workoutId = dao.getMaxWorkoutId();
        for (Workout workout : dao.getSessionWorkouts()) {
            if (workout.getRepetitions() != null && workout.getRepetitions().length != 0) {
                workout.setId(++workoutId);
                newSession.addWorkout(workout);
            }
        }

        dao.saveSession(newSession);
        resetSession();
    }

    private void resetSession() {
        dao.setSessionWorkouts(FXCollections.observableArrayList());
        sessionWorkoutsList.getItems().clear();
        currentWorkout = null;
        animationTimer.stop();

        clearExerciseInfo();
        updateTextFields();

        pauseButton.setDisable(true);
        finishButton.setDisable(true);
        nextButton.setDisable(true);
        previousButton.setDisable(true);
        startButton.setDisable(false);
        addToWorkoutButton.setDisable(true);
        removeFromWorkoutButton.setDisable(true);
        upButton.setDisable(true);
        downButton.setDisable(true);
    }

    private boolean showSummaryDialog() throws IOException {
        Stage stage = new Stage();
        Group root = new Group();
        Scene scene = new Scene(root);
        SessionSummary sessionSummary = new SessionSummary(dao, newSession, stage);

        root.getChildren().add(loadControls("/org/example/tp/ui/SessionSummary.fxml", sessionSummary));

        stage.setScene(scene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Save");
        stage.setMaxWidth(416);
        stage.setMaxHeight(539);

        stage.showAndWait();

        return !(newSession.getName() == null);
    }

    private void showStartAlert() {
        Alert alert = newAlert("Start", "Starting the session!", dao.getProperty("startRemainderText"), Alert.AlertType.INFORMATION);
        alert.showAndWait();
    }

    private void showFinishAlert() {
        Alert alert = newAlert("Finish", "Finishing the session!", dao.getProperty("endRemainderText"), Alert.AlertType.INFORMATION);
        alert.showAndWait();
    }

    public void autofill() {
        for (TextField textField : reps) {
            if (textField.isFocused() && textField.getText().isEmpty()) {
                textField.setText(textField.getPromptText());
                updateRepetitionBox();
            }
        }
    }
}
