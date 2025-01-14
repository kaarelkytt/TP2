package org.example.tp.logic;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.tp.MainApplication;
import org.example.tp.dataobjects.Exercise;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import org.example.tp.dataobjects.Workout;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Tab {
    public static void cellFactories(ObservableList<Exercise> exercises, ListView<Exercise> exerciseList) {
        exerciseList.setItems(exercises);

        exerciseList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Exercise exercise, boolean empty) {
                super.updateItem(exercise, empty);

                if (empty || exercise == null || exercise.getName() == null) {
                    setText(null);
                } else {
                    setText(exercise.getName());
                }
            }
        });
    }

    public static void cellFactoriesWithCategory(ObservableList<Exercise> exercises, ListView<Exercise> exerciseList) {
        exerciseList.setItems(exercises);

        exerciseList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Exercise exercise, boolean empty) {
                super.updateItem(exercise, empty);

                if (empty || exercise == null || exercise.getName() == null) {
                    setText(null);
                    setTextFill(Color.BLACK);
                } else {
                    switch (exercise.getCategory()){
                        case BICEPS -> setTextFill(Color.web("#FF7F50"));
                        case TRICEPS -> setTextFill(Color.web("#FFA852"));
                        case CHEST -> setTextFill(Color.web("#FF5252"));

                        case ABS -> setTextFill(Color.web("#0099CC"));
                        case BACK -> setTextFill(Color.web("#668CFF"));
                        case SHOULDERS -> setTextFill(Color.web("#7DB3E8"));

                        case LEGS -> setTextFill(Color.web("#063970"));
                    }

                    setText(String.format("%s - %s", exercise.getCategory().name().substring(0,2), exercise.getName()));
                }
            }
        });
    }

    public static void cellFactoriesWithImages(ObservableList<Workout> workouts, ListView<Workout> workoutList) {
        workoutList.setItems(workouts);

        workoutList.setCellFactory(param -> new ListCell<>() {
            private final ImageView exerciseImage = new ImageView();
            private final Label name = new Label();
            private final ImageView dumbellImage = new ImageView();
            private final VBox vBox = new VBox(name, dumbellImage);
            private final HBox hBox = new HBox(exerciseImage, vBox);

            {
                exerciseImage.setPreserveRatio(true);
                exerciseImage.setFitHeight(100);
            }


            @Override
            protected void updateItem(Workout workout, boolean empty) {
                super.updateItem(workout, empty);

                if (empty || workout == null) {
                    setGraphic(null);
                } else {
                    exerciseImage.setImage(workout.getExercise().getImage());
                    name.setText(workout.getExercise().getName());
                    dumbellImage.setImage(workout.getWeight() != 0 ? getDumbellImageByWeight(workout.getWeight()) : null);

                    setGraphic(hBox);
                }
            }
        });
    }

    public static Node loadControls(String fxml, Initializable controller) throws IOException {
        URL resource = MainApplication.class.getResource(fxml);
        if (resource == null)
            throw new IllegalArgumentException(fxml + " not found");

        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        fxmlLoader.setController(controller);
        return fxmlLoader.load();
    }

    public static String getFormattedTimeFromMillis(long millis, boolean showMillis) {
        StringBuilder time = new StringBuilder();
        if (millis < 0){
            millis *= -1;
            time.append("-");
        }

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        seconds = seconds % 60;
        millis = millis % 1000;

        time.append(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        if (showMillis) {
            time.append(String.format(".%03d", millis));
        }

        return time.toString();
    }

    public static Image getDumbellImageByWeight(float weight) {
        String dumbellSize = "empty";

        String weightString = String.valueOf(weight);
        weightString = weightString.replace(".", "_");
        if (new File("src\\main\\resources\\org\\example\\tp\\pics\\dumbell\\icon\\dumbell_" + weightString + "_tp.png").isFile()){
            dumbellSize = "dumbell_" + weightString;
        }

        return new Image("file:src\\main\\resources\\org\\example\\tp\\pics\\dumbell\\icon\\" + dumbellSize + "_tp.png");
    }

    public static Alert newAlert(String title, String header, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        return alert;
    }
}
