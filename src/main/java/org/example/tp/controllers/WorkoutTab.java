package org.example.tp.controllers;

import javafx.application.Platform;
import org.example.tp.dao.DAO;
import org.example.tp.dataobjects.*;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;

import static org.example.tp.logic.Tab.*;


public class WorkoutTab implements Initializable {
    private final DAO dao;
    private Session newSession;
    private boolean running = false;
    private int currentExerciseIndex = 0;
    private long startTimeMillis = 0;
    private long exerciseStartTimeMillis;
    private AnimationTimer animationTimer;
    private TextField[] reps;


    @FXML
    private Label exerciseName;
    @FXML
    private Label weightLabel;
    @FXML
    private Label repetitionsLabel;
    @FXML
    private Label durationLabel;
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
    private ImageView exerciseImage;
    @FXML
    private ImageView dumbellImage;

    @FXML
    private ListView<Workout> sessionWorkoutsList;

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




    public WorkoutTab(DAO dao) {
        this.dao = dao;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reps = new TextField[]{repsTextField1, repsTextField2, repsTextField3, repsTextField4, repsTextField5};
    }


    public void updateTab() {
        // DEL
        System.out.println("Updating Tab: ex #" + currentExerciseIndex);

        cellFactoriesWithImages(dao.getSessionWorkouts(), sessionWorkoutsList);

        if (sessionWorkoutsList.getItems().isEmpty()){
            clearExerciseInfo();
        } else {
            updateExerciseInfo();
        }

        estimatedDurationLabel.setText(getFormattedTimeFromMillis(dao.findEstimatedDuration(), false));
        updateButtons();
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
        elapsedTimeLabel.setText("00:00:00.000");
        startTimeLabel.setText("00:00:00");
        remainedTimeLabel.setText("00:00:00");
        estimatedEndLabel.setText("00:00:00");
        estimatedDurationLabel.setText("00:00:00");
        exerciseImage.setImage(null);
        dumbellImage.setImage(null);
        updateRepetitionBox();

        currentExerciseIndex = 0;
    }


    private void updateExerciseInfo() {
        // DEL
        System.out.println("Updating exercise info: ex #" + currentExerciseIndex);

        currentExerciseIndex = Math.min(sessionWorkoutsList.getItems().size() - 1, currentExerciseIndex);

        Exercise currentExercise = sessionWorkoutsList.getItems().get(currentExerciseIndex).getExercise();
        Workout lastWorkout = dao.findLastWorkout(currentExercise);
        Workout currentWorkout = sessionWorkoutsList.getItems().get(currentExerciseIndex);
        int[] lastReps = null;

        exerciseName.setText(currentExercise.getName());

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


        if (currentWorkout.getWeight() != 0){
            weightTextField.setText(String.valueOf(currentWorkout.getWeight()));
        }

        if (currentWorkout.getDuration() != 0){
            exerciseStartTimeMillis = System.currentTimeMillis()-currentWorkout.getDuration()*1000;
            currentExerciseDurationLabel.setText(getFormattedTimeFromMillis(currentWorkout.getDuration()*1000, false));
        } else {
            exerciseStartTimeMillis = System.currentTimeMillis();
            currentExerciseDurationLabel.setText("00:00:00");
        }

        commentTextArea.setText(currentWorkout.getComment());

        exerciseImage.setImage(currentExercise.getImage());
        exerciseImage.setPreserveRatio(true);
        exerciseImage.setFitWidth(360);

        updateDumbellImage();
        updateRepetitionBoxFocus();
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
        try {
            if (!weightTextField.getText().isBlank()) {
                float weight = Float.parseFloat(weightTextField.getText());
                dumbellImage.setImage(getDumbellImageByWeight(weight));
                sessionWorkoutsList.getItems().get(currentExerciseIndex).setWeight(weight);
                cellFactoriesWithImages(dao.getSessionWorkouts(), sessionWorkoutsList);
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

        newSession = new Session(dao.getSessions().size() + 1, LocalDateTime.now());

        startTimeLabel.setText(newSession.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        estimatedEndLabel.setText(newSession.getDateTime().plusSeconds(dao.findEstimatedDuration()/1000).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        running = true;

        startTimeMillis = System.currentTimeMillis();
        exerciseStartTimeMillis = startTimeMillis;

        animationTimer = new AnimationTimer() {
            long pauseStart = 0;

            @Override
            public void handle(long now) {

                if (running){
                    if (pauseStart != 0){
                        startTimeMillis += System.currentTimeMillis() - pauseStart;
                        pauseStart = 0;
                    }
                    long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
                    elapsedTimeLabel.setText(getFormattedTimeFromMillis(elapsedMillis, true));
                    remainedTimeLabel.setText(getFormattedTimeFromMillis(dao.findEstimatedDuration() - elapsedMillis, true));
                    currentExerciseDurationLabel.setText(getFormattedTimeFromMillis(System.currentTimeMillis() - exerciseStartTimeMillis, false));
                } else if (pauseStart == 0){
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
        if (startTimeMillis == 0){
            return;
        }

        if (!saveCurrentWorkout()){
            return;
        }

        if (!running){
            updateExerciseInfo();
        }

        running = !running;
        pauseButton.setText(running ? "Pause" : "Continue");
        exerciseImage.setImage(running ? sessionWorkoutsList.getItems().get(currentExerciseIndex).getExercise().getImage() : new Image("file:src/main/resources/org/example/tp/pics/pause.png"));
    }


    public void nextExercise() {
        if (currentExerciseIndex < sessionWorkoutsList.getItems().size() - 1){
            if (!saveCurrentWorkout()){
                return;
            }
            currentExerciseIndex ++;
            updateExerciseInfo();
        }
        updateButtons();
    }


    public void previousExercise(){
        if (currentExerciseIndex > 0){
            if (!saveCurrentWorkout()){
                return;
            }
            currentExerciseIndex --;
            updateExerciseInfo();
        }
        updateButtons();
    }


    private void updateButtons() {
        nextButton.setDisable(currentExerciseIndex >= sessionWorkoutsList.getItems().size() - 1);
        previousButton.setDisable(currentExerciseIndex <= 0);
    }


    public boolean saveCurrentWorkout() {
        // DEL
        System.out.println("Saving ex #" + currentExerciseIndex);
        System.out.println("All workouts: " + Arrays.toString(dao.getSessionWorkouts().toArray()));

        boolean saved = true;

        if (dao.getSessionWorkouts().isEmpty()){
            return saved;
        }

        Workout workout = dao.getSessionWorkouts().get(currentExerciseIndex);

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


        if (running){
            workout.setDuration((System.currentTimeMillis() - exerciseStartTimeMillis) / 1000);
        }

        workout.setComment(commentTextArea.getText());
        return saved;
    }

    private void saveSession() throws IOException {
        running = true;
        if (!saveCurrentWorkout()){
            return;
        }
        newSession.setDuration((System.currentTimeMillis() - startTimeMillis) / 1000);
        running = false;
        exerciseImage.setImage(new Image("file:src/main/resources/org/example/tp/pics/pause.png"));

        if (dao.getProperty("endRemainder").equals("true")) {
            showFinishAlert();
        }


        if (!showSummaryDialog()){
            pauseWorkout();
            return;
        }

        for (Workout workout : dao.getSessionWorkouts()) {
            newSession.addWorkout(workout);
        }

        dao.saveSession(newSession);
        resetSession();
    }

    private void resetSession() {
        dao.setSessionWorkouts(FXCollections.observableArrayList());
        dao.setSessionExercises(FXCollections.observableArrayList());
        animationTimer.stop();

        updateTab();

        pauseButton.setDisable(true);
        finishButton.setDisable(true);
        nextButton.setDisable(true);
        previousButton.setDisable(true);
        startButton.setDisable(false);
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Start");
        alert.setHeaderText("Starting the session!");
        alert.setContentText(dao.getProperty("startRemainderText"));
        alert.showAndWait();
    }

    private void showFinishAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Finish");
        alert.setHeaderText("Finishing the session!");
        alert.setContentText(dao.getProperty("endRemainderText"));
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
