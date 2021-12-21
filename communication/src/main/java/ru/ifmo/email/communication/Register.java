package ru.ifmo.email.communication;

import ru.ifmo.email.model.User;

import java.io.Serializable;

public class Register extends Command {
    public Register(User user) {
        super(user);
    }
}
