package ru.ifmo.email.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.email.client.Properties;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {
    private final static Logger log = LoggerFactory.getLogger(ConfigController.class);

    private Stage stage;
    private final Properties properties;

    @FXML
    private TextField host;
    @FXML
    private TextField port;

    public ConfigController(Stage stage) {
        this.stage = stage;
        this.properties = Properties.getProperties();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("initialize");
        host.setText(properties.getHost());
        port.setText(String.valueOf(properties.getPort()));
    }

    @FXML
    protected void save() {
        properties.setHost(host.getText());
        properties.setPort(port.getText());
        stage.close();
    }

    @FXML
    protected void cancel() {
        stage.close();
    }
}
