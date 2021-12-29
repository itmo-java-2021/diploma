package ru.ifmo.email.server.storage;

import ru.ifmo.email.model.Message;
import ru.ifmo.email.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class DbStorage implements Storage{

    @Override
    public void addUser(User user, String password) {
        try(Connection con = DataSource.getConnection())
        {
            try (PreparedStatement prepared = con.prepareStatement("INSERT INTO \"user\" (\"name\", \"email\", \"password\") VALUES (?, ?, ?);")) {

                prepared.setString(1, user.name());
                prepared.setString(2, user.email());
                prepared.setString(3, password);
                prepared.executeUpdate();

//                try (ResultSet rs = prepared.executeQuery()) {
//                    while (rs.next()) {
//                        int id = rs.getInt("id");
//                        String title = rs.getString("title");
//                        int duration = rs.getInt("duration");
//                        double price = rs.getDouble("price");
//
//                        System.out.printf("id: %s, title: %s, duration: %s, price: %s\n", id, title, duration, price);
//                    }
 //               }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean isUser(User user) {
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
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void addMessage(User user, Message message) {

    }

    @Override
    public List<Message> getMessage(User user) {

        return null;
    }

    @Override
    public User getUser(String email){
        try(Connection con = DataSource.getConnection())
        {
            try (PreparedStatement prepared = con.prepareStatement("SELECT email, password, name FROM \"user\" where \"email\" = ?;")) {

                prepared.setString(1, email);
                try (ResultSet rs = prepared.executeQuery()) {
                    while (rs.next()){
                        String email1 = rs.getString("email");
                        String password = rs.getString("password");
                        String name = rs.getString("name");
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean logIn(String email, String password) {
        Boolean result = false;
        try(Connection con = DataSource.getConnection())
        {
            try (PreparedStatement prepared = con.prepareStatement("SELECT email, password, name FROM \"user\" where \"email\" = ? limit 1;")) {

                prepared.setString(1, email);
                try (ResultSet rs = prepared.executeQuery()) {
                    while (rs.next()){
                        if (rs.getString("password") == password){
                            result = true;
                        }
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


        return result;
    }
}
