package ru.ifmo.email.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import ru.ifmo.email.client.controller.AuthorizationController;
import ru.ifmo.email.client.controller.EmailClientController;
import ru.ifmo.email.client.exception.EmailClientException;
import ru.ifmo.email.client.mock.EmailClientMock;

import java.io.IOException;

public class EmailApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, EmailClientException, ClassNotFoundException {

        AuthorizationController authorization = logIn();
        if (authorization.isResult()){
//        final String email = getParameters().getNamed().get("email");
//        validateEmail(email);
//
//        // Здесь должна быть ваша реализация клиента, вместо mock.
//        final EmailClient emailClient = new EmailClient1(email);
//        emailClient.registerMyEmail(email);
//
//
//        // Всякая всячина для отрисовки пользовательского интерфейса.
//        FXMLLoader fxmlLoader = new FXMLLoader(EmailClientController.class.getResource("email-client-view.fxml"));
//        fxmlLoader.setControllerFactory(c -> new EmailClientController(emailClient));
//        Scene sendScene = new Scene(fxmlLoader.load(), 520, 440);
//        stage.setTitle("IFMO Email client: " + email);
//        stage.setScene(sendScene);
//        stage.show();
        }
    }

    private AuthorizationController logIn() throws IOException {
        AuthorizationController controller = new AuthorizationController();
        FXMLLoader authorization = new FXMLLoader(AuthorizationController.class.getResource("authorization.fxml"));
        authorization.setControllerFactory(c -> new AuthorizationController());
        Scene scene = new Scene(authorization.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("test");
        stage.showAndWait();
        return controller;
    }

    private void validateEmail(String email) {
        if (email == null) {
            System.err.println("Необходимо запустить программу, указав обязательный параметр --email=email@server.host");
            System.err.println("где 'email' - ваш почтовый логин на сервере, а server.host - имя хоста, либо");
            System.err.println("IP адрес сервера.");
            System.exit(1);
        }

        if (!email.contains("@")) {
            System.err.println("Указанный имейл должен быть в формате: email@server.host");
            System.err.println("где 'email' - ваш почтовый логин на сервере, а server.host - имя хоста, либо");
            System.err.println("IP адрес сервера.");
            System.exit(1);
        }
    }
}