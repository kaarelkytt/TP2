package org.example.tp.controllers;

import org.example.tp.dao.DAO;
import org.example.tp.dataobjects.Session;
import org.example.tp.dataobjects.Workout;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static org.example.tp.logic.Tab.getFormattedTimeFromMillis;

public class SessionSummary implements Initializable {
    private final Session session;
    private final DAO dao;
    private final Stage stage;

    @FXML
    public TextField nameTextField;
    @FXML
    public TextArea commentTextArea;
    @FXML
    public Label durationLabel;
    @FXML
    public Label dateLabel;
    @FXML
    public TableView<Workout> exercisesTableView;
    @FXML
    public TableColumn<Workout, String> nameTableColumn;
    @FXML
    public TableColumn<Workout, Float> weightTableColumn;
    @FXML
    public TableColumn<Workout, String> repetitionTableColumn;
    @FXML
    public TableColumn<Workout, String> durationTableColumn;


    @FXML
    public void saveButtonClicked(){save();}
    @FXML
    public void cancelButtonClicked(){stage.close();}


    public SessionSummary(DAO dao, Session session, Stage stage) {
        this.dao = dao;
        this.session = session;
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        durationLabel.setText(getFormattedTimeFromMillis(session.getDuration() * 1000, false));
        dateLabel.setText(session.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd.MM.yyyy")));

        exercisesTableView.setItems(dao.getSessionWorkouts());
        nameTableColumn.setCellValueFactory(entry -> new SimpleObjectProperty<>(entry.getValue().getExercise().getName()));
        weightTableColumn.setCellValueFactory(entry -> new SimpleObjectProperty<>(entry.getValue().getWeight()));
        repetitionTableColumn.setCellValueFactory(entry -> new SimpleObjectProperty<>(entry.getValue().getRepetitionsString("-")));
        durationTableColumn.setCellValueFactory(entry -> new SimpleObjectProperty<>(getFormattedTimeFromMillis(entry.getValue().getDuration() * 1000, false)));
    }

    private void save() {
        if (nameTextField.getText().isEmpty()) {
            session.setName("Session " + session.getId());
        } else {
            session.setName(nameTextField.getText());
        }

        if (!commentTextArea.getText().isEmpty()){
            session.setComment(commentTextArea.getText());
        }

        stage.close();
    }
}
