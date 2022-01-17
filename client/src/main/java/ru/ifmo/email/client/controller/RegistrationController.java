package ru.ifmo.email.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.email.client.Encryption;
import ru.ifmo.email.client.Properties;
import ru.ifmo.email.communication.CodeResponse;
import ru.ifmo.email.communication.ICommand;
import ru.ifmo.email.communication.Registration;
import ru.ifmo.email.communication.Response;
import ru.ifmo.email.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable {
    private final static Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private Stage stage;

    @FXML
    private TextField email;
    @FXML
    private TextField server;
    @FXML
    private TextField fio;
    @FXML
    private TextField password;

    public RegistrationController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("initialize");
    }

    @FXML
    protected void registration() throws IOException, ClassNotFoundException {
        log.info("registration");
        Socket socket = new Socket(Properties.getProperties().getHost(), Properties.getProperties().getPort());
        ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());

        ICommand ICommand = new Registration(new User(0, fio.getText(), email.getText() + server.getText()), Encryption.encrypted(password.getText()));
        objOut.writeObject(ICommand);
        ICommand o = (ICommand) objIn.readObject();
        if (o instanceof Response response){{
            if (response.code() == CodeResponse.ERROR){
                log.error(response.message());
                System.out.println(response.message());

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("WARNING");
                alert.setHeaderText("error register");
                alert.setContentText(response.message());
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });

                System.exit(1);
            }
        }}

        stage.close();
    }
}
