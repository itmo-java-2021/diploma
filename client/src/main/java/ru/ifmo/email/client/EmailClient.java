package ru.ifmo.email.client;

import ru.ifmo.email.client.exception.EmailClientException;
import ru.ifmo.email.communication.*;
import ru.ifmo.email.model.Message;
import ru.ifmo.email.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class EmailClient implements IEmailClient {
    private final String host;
    private final int port;
    private final User user;

    public EmailClient(User user) {
        this.host = Properties.getProperties().getHost();
        this.port = Properties.getProperties().getPort();
        this.user = user;
    }

    @Override
    public void send(Message message, String recipient) throws EmailClientException {
        try(Socket socket = new Socket(host, port)) {
            ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
            message.setSenderAddress(user.email());

            ICommand ICommand = new SendEmail(user, message, recipient);
            objOut.writeObject(ICommand);
            ICommand o = (ICommand) objIn.readObject();
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
        try(Socket socket = new Socket(host, port)) {
            ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());

            ICommand ICommand = new LoadEmails(user);
            objOut.writeObject(ICommand);
            ICommand o = (ICommand) objIn.readObject();
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
