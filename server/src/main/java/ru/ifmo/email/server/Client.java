package ru.ifmo.email.server;

import ru.ifmo.email.model.Message;

import java.net.InetAddress;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Client {
    public final String email;
    private final List<Message> messages;

    public Client(String email) {
        this.email = email;
        this.messages = new ArrayList<Message>();
    }

    public void addMessage(Message message){
        messages.add(message);
    }

    public List<Message> getMessages(){
        return new ArrayList<>(messages);
    }
}
