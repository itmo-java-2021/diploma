package ru.ifmo.email.client;

import ru.ifmo.email.client.exception.EmailClientException;
import ru.ifmo.email.model.Message;

import java.io.IOException;
import java.util.List;

public interface IEmailClient {

    void send(Message message, String recipient) throws EmailClientException;

    List<Message> loadEmails() throws EmailClientException;
}
