package ru.ifmo.email.model;

import java.io.Serializable;

public record User(int id, String name, String email) implements Serializable {
}
