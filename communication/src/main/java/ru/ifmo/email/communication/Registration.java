package ru.ifmo.email.communication;

import ru.ifmo.email.model.User;

import java.io.Serializable;

public class Registration extends Command {
    private final String password;

    public Registration(User user, String password) {
        super(user);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
