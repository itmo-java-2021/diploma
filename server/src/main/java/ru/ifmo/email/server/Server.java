package ru.ifmo.email.server;

import ru.ifmo.email.communication.*;
import ru.ifmo.email.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
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

    private HashMap<String, Client> clients;

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

        this.port = port;

        clients = new HashMap<>();
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
                    e.printStackTrace();
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
                    final Command command = (Command) objIn.readObject();
                    //тут команды
                    if (command instanceof Register register){
                        String email = register.getEmail();
                        if (clients.containsKey(email)){
                            System.out.println("error register user: " + email);
                            final Command response = new Response(CodeResponse.ERROR, "error register user: " + email);
                            objOut.writeObject(response);
                        } else {
                            System.out.println("register user: " + email);
                            clients.put(email, new Client(email));
                            final Command response = new Response(CodeResponse.OK, "");
                            objOut.writeObject(response);
                        }
                    } else if (command instanceof SendEmail sendEmail){
                        String email = sendEmail.getRecipient();
                        if (clients.containsKey(email)){
                            clients.get(email).addMessage(sendEmail.getMessage());
                            final Command response = new Response(CodeResponse.OK, "");
                            objOut.writeObject(response);
                        } else {
                            final Command response = new Response(CodeResponse.ERROR, "user is not registered");
                            objOut.writeObject(response);
                        }
                    }
                    else if (command instanceof LoadEmails emails){
                        String email = emails.getEmail();
                        if (clients.containsKey(email)){
                            List<Message> messages = clients.get(email).getMessages();
                            final Command response = new Response(CodeResponse.OK, "", messages);
                            objOut.writeObject(response);
                        } else {
                            final Command response = new Response(CodeResponse.ERROR, "user is not registered");
                            objOut.writeObject(response);
                        }
                    }
                    interrupt();
                }

            } catch (IOException e) {
                System.err.println("Client " + socket.getInetAddress() + " disconnected");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
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
    }
}