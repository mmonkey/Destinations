package com.github.mmonkey.Destinations.Migrations;

import com.github.mmonkey.Destinations.Database.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class AddInitialDatabaseTables implements Migration {

    private Database database;

    public void up() {

        Connection connection = null;
        Statement statement = null;
        StringBuilder sql = new StringBuilder();

        // Create worlds table
        sql.append("CREATE TABLE IF NOT EXISTS worlds "
                + "(id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, uniqueId UUID NOT NULL);");

        // Create players table
        sql.append("CREATE TABLE IF NOT EXISTS players "
                + "(id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, uniqueId UUID NOT NULL);");

        // Create destinations table
        sql.append("CREATE TABLE IF NOT EXISTS destinations" +
                " (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL," +
                " worldId INT REFERENCES worlds(id)," +
                " x DOUBLE NOT NULL," +
                " y DOUBLE NOT NULL," +
                " z DOUBLE NOT NULL," +
                " yaw DOUBLE NOT NULL," +
                " pitch DOUBLE NOT NULL," +
                " roll DOUBLE NOT NULL);");

        // Create homes table
        sql.append("CREATE TABLE IF NOT EXISTS homes" +
                " (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL," +
                " destinationId INT REFERENCES destinations(id)," +
                " ownerId INT REFERENCES players(id)," +
                " name VARCHAR(255));");

        // Create warps table
        sql.append("CREATE TABLE IF NOT EXISTS warps" +
                " (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL," +
                " destinationId INT REFERENCES destinations(id)," +
                " ownerId INT REFERENCES players(id)," +
                " name VARCHAR(255)," +
                " isPublic BOOLEAN DEFAULT true);");

        // Create warp_player table
        sql.append("CREATE TABLE IF NOT EXISTS warp_player" +
                " (warpId INT REFERENCES warps(id)," +
                " playerId INT REFERENCES players(id));");

        try {

            connection = database.getConnection();
            statement = connection.createStatement();
            statement.execute(sql.toString());

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }
    }

    public void down() {

        Connection connection = null;
        Statement statement = null;
        StringBuilder sql = new StringBuilder();

        // Drop worlds table
        sql.append("DROP TABLE IF EXISTS worlds;");

        // Drop players table
        sql.append("DROP TABLE IF EXISTS players;");

        // Drop destinations table
        sql.append("DROP TABLE IF EXISTS destinations;");

        // Drop homes table
        sql.append("DROP TABLE IF EXISTS homes;");

        // Drop warps table
        sql.append("DROP TABLE IF EXISTS warps;");

        // Drop warp_player table
        sql.append("DROP TABLE IF EXISTS warp_player;");

        try {

            connection = database.getConnection();
            statement = connection.createStatement();
            statement.execute(sql.toString());

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }
    }

    public AddInitialDatabaseTables(Database database) {
        this.database = database;
    }
}
