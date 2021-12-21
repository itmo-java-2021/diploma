package ru.ifmo.email.server.storage;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by xmitya on 17.01.17.
 */
public class DataSource {
    private static volatile DataSource dataSource;
    private ComboPooledDataSource cpds;

    private DataSource() throws PropertyVetoException {
        cpds = new ComboPooledDataSource();

        cpds.setDriverClass("org.postgresql.Driver");
        cpds.setJdbcUrl("jdbc:postgresql://postgresql.local:5432/email");
        cpds.setUser("ifmo");
        cpds.setPassword("q1w2e3");

        cpds.setMinPoolSize(1);
        cpds.setMaxPoolSize(5);
        cpds.setMaxStatements(180);
    }

    public static DataSource instance() {
        if (dataSource == null) {
            synchronized (DataSource.class) {
                if (dataSource == null) {
                    try {
                        dataSource = new DataSource();
                    }
                    catch (PropertyVetoException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }

        return dataSource;
    }

    public Connection connection() throws SQLException {
        return cpds.getConnection();
    }

    public static Connection getConnection() throws SQLException {
        return instance().connection();
    }
}
