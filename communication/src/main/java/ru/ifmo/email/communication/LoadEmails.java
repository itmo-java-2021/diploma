package ru.ifmo.email.communication;

import ru.ifmo.email.model.Message;

import java.io.Serializable;
import java.util.List;

public class LoadEmails implements Serializable, Command {
    private final String email;

    public LoadEmails(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
