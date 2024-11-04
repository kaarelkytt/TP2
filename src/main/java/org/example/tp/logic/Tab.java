package org.example.tp.logic;

import org.example.tp.MainApplication;
import org.example.tp.dataobjects.Exercise;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;

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
}
