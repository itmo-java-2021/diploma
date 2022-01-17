package ru.ifmo.email.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.email.communication.*;
import ru.ifmo.email.model.Message;
import ru.ifmo.email.model.User;
import ru.ifmo.email.server.storage.DbStorage;
import ru.ifmo.email.server.storage.Storage;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.List;
import java.util.Scanner;

public class Server implements AutoCloseable {
    public static void main(String[] args) throws Exception {
        try (Server server = new Server(12345)) {
            server.start();
            Waite waite = new Waite();
            waite.waite();
        }
    }

    private final static Logger log = LoggerFactory.getLogger(Server.class);

    private Storage storage;
    private KeyPair keyPair;

    private static class Waite{

        public void waite() {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String command = scanner.nextLine();

                String[] commandArgs = command.split("\s");
                switch (commandArgs[0]) {
                    case "quit":
                        System.out.println("Bye");
                        return;
                    default:
                        System.err.println("Неизвестная команда: " + commandArgs[0]);
                        break;
                }
            }
        }
    }

    private final int port;
    private ConnectionListener listener;

    public Server(int port) {

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            keyPair = generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        this.port = port;
        storage = new DbStorage();
    }

    public synchronized void start() {
        var listener = new ConnectionListener();
        listener.start();
        this.listener = listener;
    }

    public synchronized void stop() throws InterruptedException {
        if (listener != null) {
            IOUtils.closeQuietly(listener);
            listener = null;
        }
    }

    @Override
    public void close() throws Exception {
        stop();
    }

    private class ConnectionListener extends Thread implements AutoCloseable {
        private final static Logger log = LoggerFactory.getLogger(ConnectionListener.class);
        private ServerSocket ssocket;

        @Override
        public void run() {
            try (ServerSocket ssocket = new ServerSocket(port)) {
                this.ssocket = ssocket;

                while (!isInterrupted()) {
                    final Socket socket = ssocket.accept();

                    final ConnectionServer connectionServer = new ConnectionServer(socket);
                    connectionServer.start();
                }
            } catch (IOException e) {
                // Можем пропустить вывод ошибки в консоль, если мы знаем, что это был останов.
                if (!isInterrupted()) {
                    log.error(null, e);
                }
            }
        }

        @Override
        public void close() throws Exception {
            interrupt();
            // Нужно отдельно закрывать сокет, т.к. метод accept() не выбрасывает InterruptedException,
            // а значит поток из него не выйдет по вызову метода interrupt();
            if (ssocket != null) {
                ssocket.close();
            }
        }
    }

    private class ConnectionServer extends Thread implements AutoCloseable {
        private final static Logger log = LoggerFactory.getLogger(ConnectionServer.class);
        private final Socket socket;

        private ConnectionServer(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (socket;
                 final var objIn = new ObjectInputStream(socket.getInputStream());
                 final var objOut = new ObjectOutputStream(socket.getOutputStream())) {
                while (!isInterrupted()) {
                    final ICommand ICommand = (ICommand) objIn.readObject();
                    //
                    //тут команды
                    if (ICommand instanceof LogIn logIn){
                        User user = storage.logIn(logIn.email(), decrypt(logIn.password()));
                        if (user != null){
                            final ICommand response = new Response(CodeResponse.OK, "", user);
                            objOut.writeObject(response);
                        } else {
                            final ICommand response = new Response(CodeResponse.LOGINFAILED, "wrong login or password");
                            objOut.writeObject(response);
                        }
                    } else if (ICommand instanceof Registration registration){
                        User user = registration.getUser();
                        if (storage.isUser(user)){
                            System.out.println("error register user: " + user.email());
                            final ICommand response = new Response(CodeResponse.ERROR, "error register user: " + user.email());
                            objOut.writeObject(response);
                        } else {
                            System.out.println("register user: " + user.email());
                            storage.addUser(user, decrypt(registration.getPassword()));
                            final ICommand response = new Response(CodeResponse.OK, "");
                            objOut.writeObject(response);
                        }
                    } else if (ICommand instanceof SendEmail sendEmail){
                        User user = storage.getUser(sendEmail.getRecipient());
                        if (user != null){
                            storage.addMessage(user, sendEmail.getMessage());
                            final ICommand response = new Response(CodeResponse.OK, "");
                            objOut.writeObject(response);
                        } else {
                            final ICommand response = new Response(CodeResponse.ERROR, "user is not registered");
                            objOut.writeObject(response);
                        }
                    }
                    else if (ICommand instanceof LoadEmails emails){
                        User user = emails.getUser();
                        if (storage.isUser(user)){
                            List<Message> messages = storage.getMessage(user);
                            final ICommand response = new Response(CodeResponse.OK, "", messages);
                            objOut.writeObject(response);
                        } else {
                            final ICommand response = new Response(CodeResponse.ERROR, "user is not registered");
                            objOut.writeObject(response);
                        }
                    } else if (ICommand instanceof GetPublicKey getPublicKey){
                        //PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
                        byte[] bytes = keyPair.getPublic().getEncoded();
                        final ICommand response = new Response(CodeResponse.OK, "", bytes);
                        objOut.writeObject(response);
                    }
                    interrupt();
                }

            } catch (IOException e) {
                log.error("Client " + socket.getInetAddress() + " disconnected");
            } catch (ClassNotFoundException e) {
                log.error(null, e);
            } finally {

            }
        }

        @Override
        public void close() throws Exception {
            interrupt();
            // Аналогично с ServerSocket, метод read не завершится по вызову interrupt().
            if (socket != null) {
                socket.close();
            }
        }

        private String decrypt(byte[] bytes) {
            try{
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                byte[] data = cipher.doFinal(bytes);
                return new String(data);
            }
            catch (Exception e){
                log.error(null, e);
            }
            return null;
        }
    }
}