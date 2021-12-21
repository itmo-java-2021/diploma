package ru.ifmo.email.communication;

import ru.ifmo.email.model.Message;
import ru.ifmo.email.model.User;

import java.io.Serializable;

public class SendEmail extends Command {
    private final Message message;
    private final String recipient;

    public SendEmail(User user, Message message, String recipient) {
        super(user);
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
