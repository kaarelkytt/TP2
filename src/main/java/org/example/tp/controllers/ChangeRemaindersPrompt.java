package org.example.tp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.tp.dao.DAO;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChangeRemaindersPrompt  implements Initializable {
    private final DAO dao;
    private final Stage stage;

    @FXML
    private TextArea startRemainderTextArea;
    @FXML
    private TextArea endRemainderTextArea;

    @FXML
    public void saveButtonClicked() throws IOException {
        save();
    }
    @FXML
    public void cancelButtonClicked(){
        stage.close();
    }

    public ChangeRemaindersPrompt(DAO dao, Stage stage) {
        this.dao = dao;
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startRemainderTextArea.setText(dao.getProperty("startRemainderText"));
        endRemainderTextArea.setText(dao.getProperty("endRemainderText"));
    }

    private void save() throws IOException {
        dao.setProperty("startRemainderText", startRemainderTextArea.getText());
        dao.setProperty("endRemainderText", endRemainderTextArea.getText());

        stage.close();
    }
}
