package ru.ifmo.email.communication;

import ru.ifmo.email.model.User;

public class LoadEmails extends Command {

    public LoadEmails(User user) {
        super(user);
    }
}
