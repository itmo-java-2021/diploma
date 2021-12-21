package ru.ifmo.email.communication;

import java.io.Serializable;

public final class Response implements ICommand {

    private final CodeResponse code;
    private final String message;
    private final Object o;

    public Response(CodeResponse code, String message, Object o) {
        this.code = code;
        this.message = message;
        this.o = o;
    }

    public Response(CodeResponse code, String message) {
        this.code = code;
        this.message = message;
        this.o = null;
    }

    public CodeResponse code() {
        return code;
    }

    public String message() {
        return message;
    }

    public Object o() {
        return o;
    }
}
