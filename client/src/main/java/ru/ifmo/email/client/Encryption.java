package ru.ifmo.email.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.email.client.controller.AuthorizationController;
import ru.ifmo.email.communication.CodeResponse;
import ru.ifmo.email.communication.GetPublicKey;
import ru.ifmo.email.communication.ICommand;
import ru.ifmo.email.communication.Response;

import javax.crypto.Cipher;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class Encryption {
    private final static Logger log = LoggerFactory.getLogger(Encryption.class);

    private Encryption() {
    }

    public static byte[] encrypted(String s){
        try(Socket socket = new Socket(Properties.getProperties().getHost(), Properties.getProperties().getPort())) {
            ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
            ICommand o;

            objOut.writeObject(new GetPublicKey());
            o = (ICommand) objIn.readObject();
            if (o instanceof Response response && response.code() == CodeResponse.OK){
                if (response.o() instanceof byte[] bytes){
                    PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE, key);
                    byte[] data = cipher.doFinal(s.getBytes());
                    return data;
                }
            }
        } catch (Exception e) {
            log.error(null, e);
        }
        return null;
    }
}
