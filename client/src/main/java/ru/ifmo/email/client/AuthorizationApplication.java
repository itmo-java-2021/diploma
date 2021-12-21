package ru.ifmo.email.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.ifmo.email.client.controller.AuthorizationController;

public class AuthorizationApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader authorization = new FXMLLoader(AuthorizationController.class.getResource("authorization.fxml"));
        authorization.setControllerFactory(c -> new AuthorizationController());
        Scene scene = new Scene(authorization.load());
        stage.setTitle("authorization");
        stage.setScene(scene);
        stage.show();
    }
}
