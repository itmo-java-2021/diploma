package ru.ifmo.email.client;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ru.ifmo.email.client.exception.EmailClientException;
import ru.ifmo.email.communication.*;
import ru.ifmo.email.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class EmailClient1 implements EmailClient{
    private final String host;
    private final int port;
    private final String user;

    public EmailClient1(String email) {
        var s1 = email.split("@");
        var s2 = s1[1].split(":");
        this.host = s2[0];
        this.port = Integer.parseInt(s2[1]);
        this.user = s1[0];
    }

    @Override
    public void registerMyEmail(String email) throws EmailClientException, IOException, ClassNotFoundException {
        Socket socket = new Socket(host, port);
        ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());

        Command command = new Register(user);
        objOut.writeObject(command);
        Command o = (Command) objIn.readObject();
        if (o instanceof Response response){{
            if (response.code() == CodeResponse.ERROR){
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
    }

    @Override
    public void send(Message message, String recipient) throws EmailClientException {
        try {
            Socket socket = new Socket(host, port);
            ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
            message.setSenderAddress(user);

            Command command = new SendEmail(message, recipient);
            objOut.writeObject(command);
            Command o = (Command) objIn.readObject();
            if (o instanceof Response response){{
                if (response.code() == CodeResponse.ERROR){
                    System.out.println(response.message());
                }
            }}

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> loadEmails() throws EmailClientException {
        List<Message> messages = new ArrayList<>();
        try {
            Socket socket = new Socket(host, port);
            ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());

            Command command = new LoadEmails(user);
            objOut.writeObject(command);
            Command o = (Command) objIn.readObject();
            if (o instanceof Response response){{
                switch (response.code()) {
                    case OK -> {
                        messages = (List<Message>) response.o();
                    }
                    case ERROR -> {
                        System.out.println(response.message());
                    }
                }
            }}

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
