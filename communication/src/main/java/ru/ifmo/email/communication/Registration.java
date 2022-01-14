package ru.ifmo.email.communication;

import ru.ifmo.email.model.User;

import java.io.Serializable;

public class Registration extends Command {
    private final byte[] password;

    public Registration(User user, byte[] password) {
        super(user);
        this.password = password;
    }

    public byte[] getPassword() {
        return password;
    }
}
