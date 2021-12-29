package ru.ifmo.email.communication;

public record LogIn(String email, String password) implements ICommand{
}
