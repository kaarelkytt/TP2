package org.example.tp.controllers;

import javafx.fxml.Initializable;
import org.example.tp.dao.DAO;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuBar implements Initializable {
    private DAO dao;

    public MenuBar(DAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
