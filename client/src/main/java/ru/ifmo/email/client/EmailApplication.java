package ru.ifmo.email.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.ifmo.email.client.controller.AuthorizationController;
import ru.ifmo.email.client.controller.EmailClientController;
import ru.ifmo.email.client.exception.EmailClientException;
import ru.ifmo.email.model.User;

import java.io.IOException;
import java.util.Objects;

public class EmailApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, EmailClientException, ClassNotFoundException {

        AuthorizationController authorization = logIn();
        if (authorization.isResult()){
            User user = authorization.getUser();
            // Здесь должна быть ваша реализация клиента, вместо mock.
            final IEmailClient IEmailClient = new EmailClient(user);
            // Всякая всячина для отрисовки пользовательского интерфейса.
            FXMLLoader fxmlLoader = new FXMLLoader(EmailClientController.class.getResource("email-client-view.fxml"));
            fxmlLoader.setControllerFactory(c -> new EmailClientController(IEmailClient, stage));
            Scene sendScene = new Scene(fxmlLoader.load());
            stage.setTitle("IFMO Email client: " + user.email());
            stage.setScene(sendScene);
            stage.show();
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/Mail_16x.png"))));
        }
    }

    private AuthorizationController logIn() throws IOException {
        Stage stage = new Stage();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/Mail_16x.png"))));
        AuthorizationController controller = new AuthorizationController(stage);
        FXMLLoader authorization = new FXMLLoader(AuthorizationController.class.getResource("authorization.fxml"));
        authorization.setControllerFactory(c -> controller);
        Scene scene = new Scene(authorization.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("authorization");
        stage.showAndWait();
        return controller;
    }
}