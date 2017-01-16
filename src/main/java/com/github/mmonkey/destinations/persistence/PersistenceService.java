package com.github.mmonkey.destinations.persistence;

import com.google.common.base.Preconditions;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PersistenceService {

    private static PersistenceService instance;
    private final Configuration configuration;
    private final String type;
    private final String url;
    private final String database;
    private final String username;
    private final String password;

    private SessionFactory sessionFactory;

    /**
     * PersistenceService constructor
     */
    public PersistenceService(Configuration configuration, String type, String url, String database, String username, String password)
            throws IOException, ClassNotFoundException {
        Preconditions.checkNotNull(configuration);
        Preconditions.checkArgument(!type.isEmpty());
        Preconditions.checkArgument(!url.isEmpty());
        Preconditions.checkArgument(!database.isEmpty());
        Preconditions.checkArgument(!username.isEmpty());
        Preconditions.checkArgument(!password.isEmpty());

        instance = this;
        this.configuration = configuration;
        this.type = type;
        this.url = url;
        this.database = database;
        this.username = username;
        this.password = password;
        this.initialize();
    }

    /**
     * @return PersistenceService
     */
    public static PersistenceService getInstance() {
        return instance;
    }

    /**
     * @return SessionFactory
     */
    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    /**
     * Initialize the database connection
     */
    private void initialize() throws IOException, ClassNotFoundException {
        Properties properties = new Properties();

        // Dynamically load the database properties based on type
        InputStream databaseProperties = getClass().getClassLoader().getResourceAsStream(this.type.toLowerCase() + ".properties");
        properties.load(databaseProperties);
        databaseProperties.close();

        // Load HikariCP properties
        InputStream connectionPoolProperties = getClass().getClassLoader().getResourceAsStream("hikari.properties");
        properties.load(connectionPoolProperties);
        connectionPoolProperties.close();

        // Updated database connection properties
        properties.setProperty("hibernate.connection.url", url + database);
        properties.setProperty("hibernate.connection.username", username);
        properties.setProperty("hibernate.connection.password", password);
        this.configuration.addProperties(properties);

        // Load the driver class for this database type
        Class.forName(properties.getProperty("hibernate.connection.driver_class"));

        // Create the session factory
        ServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        this.sessionFactory = configuration.buildSessionFactory(registry);
    }

}
