package ru.ifmo.email.model;

import java.io.Serializable;

public record User(String name, String email) implements Serializable {
}
