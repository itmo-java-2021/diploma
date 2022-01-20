package ru.ifmo.email.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.email.client.Encryption;
import ru.ifmo.email.client.Properties;
import ru.ifmo.email.communication.*;
import ru.ifmo.email.model.User;

import javax.crypto.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;
import java.util.ResourceBundle;

public class AuthorizationController implements Initializable {
    private final static Logger log = LoggerFactory.getLogger(AuthorizationController.class);

    private boolean result;
    private User user;
    private Stage stage;

    @FXML
    private TextField email;

    @FXML
    private TextField password;

    @FXML
    private ImageView imageView;

    @FXML
    private Pane pane;

    public AuthorizationController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/Settings_16x.png")));
        imageView.setImage(image);
        log.info("initialize");
    }

    @FXML
    protected void registration() throws IOException {
        Stage stage = new Stage();
        RegistrationController controller = new RegistrationController(stage);
        FXMLLoader fxmlLoader = new FXMLLoader(RegistrationController.class.getResource("registration.fxml"));
        fxmlLoader.setControllerFactory(c -> controller);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("registration");
        stage.showAndWait();
    }

    @FXML
    protected void logIn() {
        try(Socket socket = new Socket(Properties.getProperties().getHost(), Properties.getProperties().getPort())) {
            ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
            ICommand o;

            objOut.writeObject(new LogIn(email.getText(), Encryption.encrypted(password.getText())));
            o = (ICommand) objIn.readObject();
            if (o instanceof Response response){{
                if (response.code() == CodeResponse.OK){
                    if (response.o() instanceof User user){
                        this.user = user;
                        result = true;
                        stage.close();
                        log.info("logIn: {}", user.email());
                    }
                } else if (response.code() == CodeResponse.LOGINFAILED){
                    pane.setVisible(true);
                    pane.setPrefHeight(Pane.USE_COMPUTED_SIZE);
                    log.info("login failed: {}", email.getText());
                } else {
                    log.warn(response.message());
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("warning");
                    alert.setHeaderText("warning");
                    alert.setContentText(response.message());
                    alert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK.");
                        }
                    });
                }
            }}
        } catch (Exception e) {
            log.error(null, e);
        }
    }

    @FXML
    protected void setting() {
        try{
            Stage stage = new Stage();
            ConfigController controller = new ConfigController(stage);
            FXMLLoader fxmlLoader = new FXMLLoader(ConfigController.class.getResource("config.fxml"));
            fxmlLoader.setControllerFactory(c -> controller);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            //stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("authorization");
            stage.showAndWait();
        }
        catch (Exception e){
            log.error(null, e);
        }
    }

    public boolean isResult() {
        return result;
    }

    public User getUser() {
        return user;
    }
}
