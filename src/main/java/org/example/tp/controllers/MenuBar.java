package org.example.tp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.tp.dao.DAO;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.example.tp.logic.ExportData.exercisesToJson;
import static org.example.tp.logic.ExportData.historyToJson;
import static org.example.tp.logic.ImportData.exercisesFromJson;
import static org.example.tp.logic.ImportData.historyFromJson;
import static org.example.tp.logic.Tab.loadControls;

public class MenuBar implements Initializable {
    private DAO dao;

    @FXML
    private CheckMenuItem startRemainderCheckButton;
    @FXML
    private CheckMenuItem endRemainderCheckButton;
    @FXML
    private MenuItem editRemaindersMenuItem;

    @FXML
    public void importHistoryClicked() throws IOException {
        importHistory();
    }
    @FXML
    public void importExercisesClicked() throws IOException {
        importExercises();
    }
    @FXML
    public void exportHistoryClicked() throws IOException {
        exportHistory();
    }
    @FXML
    public void exportExercisesClicked() throws IOException {
        exportExercises();
    }

    @FXML
    public void startRemainderCheckButtonClicked() throws IOException {
        updateRemainderState("startRemainder", startRemainderCheckButton);
    }

    @FXML
    public void endRemainderCheckButtonClicked() throws IOException {
        updateRemainderState("endRemainder", endRemainderCheckButton);
    }

    @FXML
    public void editRemaindersMenuItemClicked() throws IOException {
        showChangeRemainderPromt();
    }


    public MenuBar(DAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startRemainderCheckButton.setSelected(dao.getProperty("startRemainder").equals("true"));
        endRemainderCheckButton.setSelected(dao.getProperty("endRemainder").equals("true"));
    }

    private void importHistory() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Sessions");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showOpenDialog(null);
        historyFromJson(file, dao);
    }

    private void importExercises() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Exercises");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showOpenDialog(null);
        exercisesFromJson(file, dao);
    }

    private void exportHistory() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Sessions");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        fileChooser.setInitialFileName("history.json");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads"));
        File file = fileChooser.showSaveDialog(null);
        historyToJson(file, dao);
    }

    private void exportExercises() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Exercises");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        fileChooser.setInitialFileName("exercises.json");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Downloads"));
        File file = fileChooser.showSaveDialog(null);
        exercisesToJson(file, dao);
    }

    private void updateRemainderState(String remainder, CheckMenuItem CheckButton) throws IOException {
        if (CheckButton.isSelected()) {
            dao.setProperty(remainder, "true");
        } else {
            dao.setProperty(remainder, "false");
        }
    }

    private void showChangeRemainderPromt() throws IOException {
        Stage stage = new Stage();
        Group root = new Group();
        Scene scene = new Scene(root);
        ChangeRemaindersPrompt changeRemaindersPrompt = new ChangeRemaindersPrompt(dao,  stage);

        root.getChildren().add(loadControls("/org/example/tp/ui/ChangeRemainders.fxml", changeRemaindersPrompt));

        stage.setScene(scene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Edit remainders");

        stage.showAndWait();
    }
}
