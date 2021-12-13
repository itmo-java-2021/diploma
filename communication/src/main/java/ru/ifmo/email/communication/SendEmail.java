package ru.ifmo.email.communication;

import ru.ifmo.email.model.Message;

import java.io.Serializable;

public class SendEmail implements Serializable, Command {
    private final Message message;
    private final String recipient;

    public SendEmail(Message message, String recipient) {
        this.message = message;
        this.recipient = recipient;
    }

    public Message getMessage() {
        return message;
    }

    public String getRecipient() {
        return recipient;
    }
}
