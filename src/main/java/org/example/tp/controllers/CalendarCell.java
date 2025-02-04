package org.example.tp.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.example.tp.dao.DAO;
import org.example.tp.dataobjects.Session;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class CalendarCell implements Initializable {
    private final DAO dao;
    private LocalDate date;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label dayLabel;

    @FXML
    private VBox sessionsVBox;

    public CalendarCell(DAO dao) {
        this.dao = dao;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        anchorPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1, 0, 0, 1))));

        anchorPane.setOnMouseEntered(event -> {
            anchorPane.setOpacity(anchorPane.getOpacity() + 0.2);
        });
        anchorPane.setOnMouseExited(event -> {
            anchorPane.setOpacity(anchorPane.getOpacity() - 0.2);
        });
    }

    public void update(LocalDate newDate, boolean isCurrentMonth) {
        date = newDate;
        double opacity = isCurrentMonth ? 0.8 : 0.3;

        dayLabel.setText(String.valueOf(date.getDayOfMonth()));
        sessionsVBox.getChildren().clear();
        anchorPane.setOpacity(opacity);

        List<Session> sessions = dao.getDateSessions(date);
        if (sessions.isEmpty()) {
            if (isCurrentMonth){
                anchorPane.setStyle("-fx-background-color: #FFFFFF");
            } else {
                anchorPane.setStyle("-fx-background-color: #DDDDDD");
            }
        } else{
            anchorPane.setStyle("-fx-background-color: #96C841");
            for (Session session : sessions) {
                sessionsVBox.getChildren().add(new Label(session.getName()));
            }
        }

    }
}
