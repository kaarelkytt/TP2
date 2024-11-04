package org.example.tp;

import org.example.tp.dao.DAO;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.tp.controllers.ExerciseTab;
import org.example.tp.controllers.WorkoutTab;

import java.io.IOException;

import static org.example.tp.logic.Tab.loadControls;

public class MainApplication extends Application {

    private final DAO dao;

    public MainApplication() throws IOException {
        dao = new DAO();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Tab workoutTab = new Tab();
        workoutTab.setText("Workout");
        workoutTab.setClosable(false);
        WorkoutTab workoutTabContoller = new WorkoutTab(dao);
        workoutTab.setContent(loadControls("WorkoutTab.fxml", workoutTabContoller));

        Tab exerciseTab = new Tab();
        exerciseTab.setText("Exercise");
        exerciseTab.setClosable(false);
        ExerciseTab exerciseTabController = new ExerciseTab(dao);
        exerciseTab.setContent(loadControls("ExerciseTab.fxml", exerciseTabController));

        TabPane tabPane = new TabPane(exerciseTab, workoutTab);
        tabPane.setOnMouseClicked(event -> {
            workoutTabContoller.updateTab();
            exerciseTabController.updateTab();
        });
        tabPane.setId("mainTabPane");

        Group root = new Group();
        Scene scene = new Scene(root, 1000, 600, Color.WHITE);
        //scene.getStylesheets().add(getClass().getResource("Theme.css").toExternalForm());

        scene.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode() == KeyCode.LEFT && scene.getFocusOwner().getId().equals("mainTabPane") && workoutTab.isSelected()) {
                workoutTabContoller.previousExercise();
                ke.consume();
            } else if (ke.getCode() == KeyCode.RIGHT && scene.getFocusOwner().getId().equals("mainTabPane") && workoutTab.isSelected()) {
                workoutTabContoller.nextExercise();
                ke.consume();
            } else if (ke.getCode() == KeyCode.SPACE && scene.getFocusOwner().getId().equals("mainTabPane") && workoutTab.isSelected()) {
                workoutTabContoller.pauseWorkout();
                ke.consume();
            }
        });

        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        borderPane.setCenter(tabPane);
        root.getChildren().add(borderPane);

        primaryStage.setTitle("Workout");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1150);
        primaryStage.setMinHeight(665);
        primaryStage.getIcons().add(new Image("org\\example\\tp\\pics\\test.png"));
        primaryStage.show();
    }
}
