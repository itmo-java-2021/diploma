package ru.ifmo.email.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AuthorizationController {
    private boolean result;

    @FXML
    private TextField login;

    @FXML
    private TextField password;

    @FXML
    protected void register() {
    }

    @FXML
    protected void logIn() {
        result = true;
    }

    public boolean isResult() {
        return result;
    }
}
