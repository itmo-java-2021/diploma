package ru.ifmo.email.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.email.client.controller.AuthorizationController;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Properties {
    private final static Logger log = LoggerFactory.getLogger(Properties.class);

    private final String prop = "client/src/main/resources/config.properties";

    private static java.util.Properties properties1;
    private static Properties properties;

    private String host;
    private int port;


    private Properties() {
        properties1 = new java.util.Properties();
        try (var file = new FileInputStream(prop)) {
            properties1.load(file);
            this.host = properties1.getProperty("host");
            this.port = Integer.parseInt(properties1.getProperty("port"));
            log.info("get and read host: {}", host);
            log.info("get and read port: {}", port);
        } catch (IOException e) {
            log.error(null, e);
        }
    }

    public static Properties getProperties(){
        if (properties == null) {
            properties = new Properties();
        }

        return properties;
    }

    public String getHost() {
        log.info("get host: {}", host);
        return host;
    }

    public int getPort() {
        log.info("get port: {}", port);
        return port;
    }

    public void setHost(String host) {
        try(var file = new FileOutputStream(prop)) {
            this.host = host;
            properties1.setProperty("host", host);
            properties1.store(file, null);
            log.info("set and write host: {}", host);
        } catch (Exception e) {
            log.error(null, e);
        }
    }

    public void setPort(String port) {
        try(var file = new FileOutputStream(prop)) {
            this.port = Integer.parseInt(port);
            properties1.setProperty("port", port);
            properties1.store(file, null);
            log.info("set and write port: {}", port);
        } catch (Exception e) {
            log.error(null, e);
        }
    }
}
