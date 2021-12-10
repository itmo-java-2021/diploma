package ru.ifmo.email.communication;

import java.io.Serializable;

public class Register implements Command, Serializable {
    private final String email;


    public Register(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
