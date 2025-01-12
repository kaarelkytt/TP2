package org.example.tp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.tp.dao.DAO;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
