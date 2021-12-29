package ru.ifmo.email.server.storage;

import ru.ifmo.email.model.Message;
import ru.ifmo.email.model.User;

import java.lang.ref.Cleaner;
import java.util.List;

public interface Storage {
    void addUser(User user, String password);
    boolean isUser(User user);
    void addMessage(User user, Message message);
    List<Message> getMessage(User user);
    User getUser(String email);
    Boolean logIn(String email, String password);
}
