package ru.ifmo.email.communication;

import java.io.Serial;
import java.util.Objects;

public final class LogIn implements ICommand {
    private final String email;
    private final byte[] password;

    public LogIn(String email, byte[] password) {
        this.email = email;
        this.password = password;
    }

    public String email() {
        return email;
    }

    public byte[] password() {
        return password;
    }

}
