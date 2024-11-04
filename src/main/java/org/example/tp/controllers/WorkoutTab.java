package org.example.tp.controllers;

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
    private ImageView nextExerciseImage;

    @FXML
    private ListView<Exercise> selectedExercisesList;

    @FXML
    public void startButtonClicked() {
        startSession();
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
    public void repetitionBoxUpdate() {
        updateRepetitionBox();
    }
    @FXML
    public void finishButtonClicked() throws IOException {
        saveSession();
    }




    public WorkoutTab(DAO dao) {
        this.dao = dao;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }


    public void updateTab() {
        cellFactories(dao.getSessionExercises(), selectedExercisesList);

        if (selectedExercisesList.getItems().isEmpty()){
            clearExerciseInfo();
        } else if (exerciseName.getText().isEmpty()) {
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
        nextExerciseImage.setImage(null);
        updateRepetitionBox();

        currentExerciseIndex = 0;
    }


    private void updateExerciseInfo() {
        Exercise currentExercise = selectedExercisesList.getItems().get(currentExerciseIndex);
        Workout lastWorkout = dao.findLastWorkout(currentExercise);
        Workout currentWorkout = dao.getSessionWorkouts().get(currentExerciseIndex);

        exerciseName.setText(currentExercise.getName());

        if (lastWorkout != null) {
            weightLabel.setText(Float.toString(lastWorkout.getWeight()));
            weightTextField.setText(Float.toString(lastWorkout.getWeight()));
            repetitionsLabel.setText(lastWorkout.getRepetitionsString(" - "));
            durationLabel.setText(lastWorkout.getDurationString());
            commentTextArea.setPromptText(lastWorkout.getComment());
        } else {
            weightLabel.setText("NaN");
            weightTextField.setText("");
            repetitionsLabel.setText("NaN");
            durationLabel.setText("NaN");
            commentTextArea.setPromptText(null);
        }

        TextField[] reps = new TextField[]{repsTextField1, repsTextField2, repsTextField3, repsTextField4, repsTextField5};
        int[] currentReps = currentWorkout.getRepetitions();

        for (int i = 0; i < reps.length; i++) {
            if (currentReps != null && i < currentReps.length) {
                reps[i].setText(String.valueOf(currentReps[i]));
            } else {
                reps[i].setText("");
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

        if (selectedExercisesList.getItems().size() > currentExerciseIndex + 1){
            nextExerciseImage.setImage(selectedExercisesList.getItems().get(currentExerciseIndex + 1).getImage());
        } else {
            nextExerciseImage.setImage(null);
        }
    }


    private void updateRepetitionBox() {
        repsTextField2.setVisible(!repsTextField1.getText().isEmpty());
        repsTextField3.setVisible(!repsTextField2.getText().isEmpty());
        repsTextField4.setVisible(!repsTextField3.getText().isEmpty());
        repsTextField5.setVisible(!repsTextField4.getText().isEmpty());
    }


    private void startSession() {
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

        saveCurrentWorkout();

        if (!running) {
            updateExerciseInfo();
        }

        running = !running;
        pauseButton.setText(running ? "Pause" : "Continue");
        exerciseImage.setImage(running ? selectedExercisesList.getItems().get(currentExerciseIndex).getImage() : new Image("org/example/tp/pics/pause.png"));
    }


    public void nextExercise() {
        if (currentExerciseIndex < selectedExercisesList.getItems().size() - 1){
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
        nextButton.setDisable(currentExerciseIndex >= selectedExercisesList.getItems().size() - 1);
        previousButton.setDisable(currentExerciseIndex <= 0);
    }


    private boolean saveCurrentWorkout() {
        Workout workout = dao.getSessionWorkouts().get(currentExerciseIndex);
        TextField[] reps = new TextField[]{repsTextField1, repsTextField2, repsTextField3, repsTextField4, repsTextField5};
        boolean saved = true;

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
        saveCurrentWorkout();
        newSession.setDuration((System.currentTimeMillis() - startTimeMillis) / 1000);
        running = false;
        exerciseImage.setImage(new Image("org/example/tp/pics/pause.png"));

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

        root.getChildren().add(loadControls("SessionSummary.fxml", sessionSummary));

        stage.setScene(scene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Save");
        stage.setMaxWidth(416);
        stage.setMaxHeight(539);

        stage.showAndWait();

        return !(newSession.getName() == null);
    }

}
