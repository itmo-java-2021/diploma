package ru.ifmo.email.communication;

import ru.ifmo.email.model.User;

public abstract class Command implements ICommand {
    private final User user;

    protected Command(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
