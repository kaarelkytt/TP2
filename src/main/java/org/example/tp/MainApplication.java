package org.example.tp;

import javafx.scene.control.*;
import org.example.tp.controllers.MenuBar;
import org.example.tp.controllers.NewWorkoutTab;
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

        Tab newWorkoutTab = new Tab("New Workout");
        newWorkoutTab.setClosable(false);
        NewWorkoutTab newWorkoutTabContoller = new NewWorkoutTab(dao);
        newWorkoutTab.setContent(loadControls("/org/example/tp/ui/NewWorkoutTab.fxml", newWorkoutTabContoller));

        TabPane tabPane = new TabPane(newWorkoutTab);

        tabPane.setId("mainTabPane");

        Group root = new Group();
        Scene scene = new Scene(root, 1000, 600, Color.WHITE);
        //scene.getStylesheets().add(getClass().getResource("Theme.css").toExternalForm());

        scene.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode() == KeyCode.PAGE_UP && newWorkoutTab.isSelected()) {
                newWorkoutTabContoller.previousExercise();
                ke.consume();
            } else if (ke.getCode() == KeyCode.PAGE_DOWN && newWorkoutTab.isSelected()) {
                newWorkoutTabContoller.nextExercise();
                ke.consume();
            } else if (ke.getCode() == KeyCode.END && newWorkoutTab.isSelected()) {
                newWorkoutTabContoller.pauseWorkout();
                ke.consume();
            } else if (ke.getCode() == KeyCode.TAB && newWorkoutTab.isSelected()) {
                newWorkoutTabContoller.autofill();
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
        primaryStage.setMinWidth(1400);
        primaryStage.setMinHeight(800);
        primaryStage.getIcons().add(new Image("file:src\\main\\resources\\org\\example\\tp\\pics\\icon.png"));

        primaryStage.show();
    }
}
