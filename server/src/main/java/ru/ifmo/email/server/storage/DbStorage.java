package ru.ifmo.email.server.storage;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.email.model.Message;
import ru.ifmo.email.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DbStorage implements Storage{
    private final static Logger log = LoggerFactory.getLogger(DbStorage.class);

    @Override
    public void addUser(User user, String password) {
        log.info("add user {}", user.email());
        try(Connection con = DataSource.getConnection())
        {
            try (PreparedStatement prepared = con.prepareStatement("INSERT INTO \"user\" (\"name\", \"email\", \"password\") VALUES (?, ?, ?);")) {

                prepared.setString(1, user.name());
                prepared.setString(2, user.email());
                String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                prepared.setString(3, bcryptHashString);
                prepared.executeUpdate();
            }
        }
        catch (Exception e){
            log.error(null, e);
        }
    }

    @Override
    public boolean isUser(User user) {
        log.info("is user {}", user.email());
        try(Connection con = DataSource.getConnection())
        {
            try (PreparedStatement prepared = con.prepareStatement("SELECT count(*) FROM \"user\" where \"email\" = ?;")) {

                prepared.setString(1, user.email());
                try (ResultSet rs = prepared.executeQuery()) {
                    rs.next();
                    int id = rs.getInt("count");
                    if (id > 0 ) return true;
               }
            }
        }
        catch (Exception e){
            log.error(null, e);
        }
        return false;
    }

    @Override
    public void addMessage(User user, Message message) {
        log.info("add message");
        try(Connection con = DataSource.getConnection())
        {
            try (PreparedStatement prepared = con.prepareStatement("INSERT INTO \"message\" (\"title\", \"text\", \"senderAddress\", \"id_user\") VALUES (?, ?, ?, ?);")) {

                prepared.setString(1, message.getTitle());
                prepared.setString(2, message.getContent());
                prepared.setString(3, message.getSenderAddress());
                prepared.setInt(4, user.id());
                prepared.executeUpdate();
            }
        }
        catch (Exception e){
            log.error(null, e);
        }
    }

    @Override
    public List<Message> getMessage(User user) {
        log.info("get message");
        List<Message> messages = new ArrayList<>();
        try(Connection con = DataSource.getConnection())
        {
            try (PreparedStatement prepared = con.prepareStatement("SELECT \"id\", \"senderAddress\", \"title\", \"text\" FROM \"message\" where \"id_user\" = ?;")) {

                prepared.setInt(1, user.id());
                try (ResultSet rs = prepared.executeQuery()) {
                    while (rs.next()){
                        Message message = new Message(rs.getInt("id"), rs.getString("senderAddress"), rs.getString("title"), rs.getString("text"));
                        messages.add(message);
                    }
                }
            }
        }
        catch (Exception e){
            log.error(null, e);
        }
        return messages;
    }

    @Override
    public User getUser(String email){
        log.info("getUser");
        RUser rUser = getRUser(email);
        if (rUser == null){
            return null;
        } else {
            return new User(rUser.id, rUser.name, rUser.email);
        }
    }

    @Override
    public User logIn(String email, String password) {
        log.info("logIn");
        RUser rUser = getRUser(email);
        if (rUser != null && BCrypt.verifyer().verify(password.toCharArray(), rUser.password).verified){
            return new User(rUser.id, rUser.name, rUser.email);
        }
        return null;
    }

    private RUser getRUser(String email){
        try(Connection con = DataSource.getConnection())
        {
            try (PreparedStatement prepared = con.prepareStatement("SELECT id, email, password, name FROM \"user\" where \"email\" = ? limit 1;")) {

                prepared.setString(1, email);
                try (ResultSet rs = prepared.executeQuery()) {
                    while (rs.next()){
                        return new RUser(rs.getInt("id"), rs.getString("email"), rs.getString("name"), rs.getString("password"));
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private record RUser(int id, String email, String name, String password) {
    }
}
