package ru.ifmo.email.client;


import java.io.FileInputStream;
import java.io.IOException;

public class Properties {
    private static java.util.Properties properties1;
    private static Properties properties;

    private String host;
    private int port;


    private Properties() {
        properties1 = new java.util.Properties();
        try {
            properties1.load(new FileInputStream("client/src/main/resources/config.properties"));
            this.host = properties1.getProperty("host");
            this.port = Integer.parseInt(properties1.getProperty("port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties getProperties(){
        if (properties == null) {
            properties = new Properties();
        }

        return properties;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
