package org.example.tp.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import org.example.tp.dao.DAO;
import org.example.tp.dataobjects.Workout;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.example.tp.logic.Tab.*;

public class StatisticsTab implements Initializable {
    private final DAO dao;
    private final LocalDate date = LocalDate.now();
    private YearMonth yearMonth = YearMonth.of(date.getYear(), date.getMonth());
    private final List<CalendarCell> calendarCells = new ArrayList<>();

    @FXML
    private GridPane gridPane;

    @FXML
    private ListView<Workout> sessionListView;

    @FXML
    private Label monthLabel;
    @FXML
    private Label yearLabel;

    @FXML
    private void previousMonthClicked() {
        yearMonth = yearMonth.minusMonths(1);
        updateCalendar();
        updateLabels();
    }
    @FXML
    private void nextMonthClicked() {
        yearMonth = yearMonth.plusMonths(1);
        updateCalendar();
        updateLabels();
    }
    @FXML
    private void previousYearClicked() {
        yearMonth = yearMonth.minusYears(1);
        updateCalendar();
        updateLabels();
    }
    @FXML
    private void nextYearClicked() {
        yearMonth = yearMonth.plusYears(1);
        updateCalendar();
        updateLabels();
    }
    @FXML
    private void loadWorkoutsClicked() {
        loadWorkouts();
    }

    public StatisticsTab(DAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initCalendar();
        initSessionListView();
        updateLabels();
        updateCalendar();
    }

    private void initSessionListView() {
        cellFactoriesWithImages(dao.getOldSessionWorkouts(), sessionListView);
    }

    private void updateLabels() {
        monthLabel.setText(capitalize(yearMonth.getMonth().toString()));
        yearLabel.setText(String.valueOf(yearMonth.getYear()));
    }

    private void initCalendar(){
        for (int i = 0; i < 42; i++) {
            try {
                CalendarCell dayCell = new CalendarCell(dao);
                Node cell = loadControls("/org/example/tp/ui/CalendarCell.fxml", dayCell);
                gridPane.add(cell, i % 7, i / 7 + 1);
                calendarCells.add(dayCell);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateCalendar() {
        int start = yearMonth.atDay(1).getDayOfWeek().getValue() - 1;
        int lengthOfMonth = yearMonth.lengthOfMonth();
        YearMonth previousYearMonth = yearMonth.minusMonths(1);
        YearMonth nextYearMonth = yearMonth.plusMonths(1);


        for (int i = 0; i < 42; i++) {
            if(i < start){
                // previous month
                calendarCells.get(i).update(LocalDate.of(previousYearMonth.getYear(), previousYearMonth.getMonth(), previousYearMonth.lengthOfMonth() - start + i + 1), false);
            } else if(i > start + lengthOfMonth - 1){
                // next month
                calendarCells.get(i).update(LocalDate.of(nextYearMonth.getYear(), nextYearMonth.getMonth(), i - start - lengthOfMonth + 1), false);
            } else {
                // current month
                calendarCells.get(i).update(LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), i - start + 1), true);
            }
        }
    }

    private void loadWorkouts() {
        dao.getSessionWorkouts().clear();
        for (Workout oldWorkout : dao.getOldSessionWorkouts()) {
            float weight = dao.findLastWorkout(oldWorkout.getExercise()).getWeight();
            Workout newWorkout = new Workout(oldWorkout.getExercise(), LocalDate.now(), weight);
            dao.getSessionWorkouts().add(newWorkout);
        }
    }
}
