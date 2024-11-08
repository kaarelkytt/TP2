package org.example.tp;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.example.tp.controllers.MenuBar;
import org.example.tp.dao.DAO;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
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

        Tab workoutTab = new Tab("Workout");
        workoutTab.setClosable(false);
        WorkoutTab workoutTabContoller = new WorkoutTab(dao);
        workoutTab.setContent(loadControls("/org/example/tp/ui/WorkoutTab.fxml", workoutTabContoller));

        workoutTab.setOnSelectionChanged (e ->{
            if (!workoutTab.isSelected()){
                if (!workoutTabContoller.saveCurrentWorkout()){
                    // TODO et tabe ei vahetataks
                    e.consume();
                }
            }
        });

        Tab exerciseTab = new Tab();
        exerciseTab.setText("Exercise");
        exerciseTab.setClosable(false);
        ExerciseTab exerciseTabController = new ExerciseTab(dao);
        exerciseTab.setContent(loadControls("/org/example/tp/ui/ExerciseTab.fxml", exerciseTabController));

        TabPane tabPane = new TabPane(exerciseTab, workoutTab);

        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (ov, oldTab, newTab) -> {
                    // DEL
                    System.out.println("Tab Selection changed");

                    workoutTabContoller.updateTab();
                    exerciseTabController.updateTab();
                }
        );

        tabPane.setId("mainTabPane");

        Group root = new Group();
        Scene scene = new Scene(root, 1000, 600, Color.WHITE);
        //scene.getStylesheets().add(getClass().getResource("Theme.css").toExternalForm());

        scene.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode() == KeyCode.PAGE_UP && workoutTab.isSelected()) {
                workoutTabContoller.previousExercise();
                ke.consume();
            } else if (ke.getCode() == KeyCode.PAGE_DOWN && workoutTab.isSelected()) {
                workoutTabContoller.nextExercise();
                ke.consume();
            } else if (ke.getCode() == KeyCode.END && workoutTab.isSelected()) {
                workoutTabContoller.pauseWorkout();
                ke.consume();
            } else if (ke.getCode() == KeyCode.TAB && workoutTab.isSelected()) {
                workoutTabContoller.autofill();
            }
        });

        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        root.getChildren().add(borderPane);

        borderPane.setCenter(tabPane);
        borderPane.setTop(loadControls("/org/example/tp/ui/MenuBar.fxml", new MenuBar(dao)));

        primaryStage.setTitle("Workout");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1150);
        primaryStage.setMinHeight(690);
        primaryStage.getIcons().add(new Image("file:src\\main\\resources\\org\\example\\tp\\pics\\icon.png"));

        primaryStage.show();
    }
}
