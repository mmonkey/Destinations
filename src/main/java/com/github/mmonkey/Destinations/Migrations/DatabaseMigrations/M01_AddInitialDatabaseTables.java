package com.github.mmonkey.Destinations.Migrations.DatabaseMigrations;

import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Database.Database;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Migrations.Migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class M01_AddInitialDatabaseTables extends Migration {

    private Database database;

    public void up() {

        Connection connection = null;
        Statement statement = null;
        StringBuilder sql = new StringBuilder();

        // Create worlds table
        sql.append("CREATE TABLE IF NOT EXISTS worlds "
                + "(id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, unique_id VARCHAR(255) NOT NULL);");

        // Create players table
        sql.append("CREATE TABLE IF NOT EXISTS players "
                + "(id INT PRIMARY KEY AUTO_INCREMENT NOT NULL, unique_id VARCHAR(255) NOT NULL);");

        // Create destinations table
        sql.append("CREATE TABLE IF NOT EXISTS destinations" +
                " (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL," +
                " world_id INT NOT NULL," +
                " x DOUBLE NOT NULL," +
                " y DOUBLE NOT NULL," +
                " z DOUBLE NOT NULL," +
                " yaw DOUBLE NOT NULL," +
                " pitch DOUBLE NOT NULL," +
                " roll DOUBLE NOT NULL);");

        // Create homes table
        sql.append("CREATE TABLE IF NOT EXISTS homes" +
                " (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL," +
                " destination_id INT NOT NULL," +
                " owner_id INT NOT NULL," +
                " name VARCHAR(255));");

        // Create warps table
        sql.append("CREATE TABLE IF NOT EXISTS warps" +
                " (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL," +
                " destination_id INT NOT NULL," +
                " owner_id INT NOT NULL," +
                " name VARCHAR(255)," +
                " is_public BOOLEAN DEFAULT true);");

        // Create warp_player table
        sql.append("CREATE TABLE IF NOT EXISTS warp_players" +
                " (warp_id INT NOT NULL," +
                " player_id INT NOT NULL," +
                " can_edit BOOLEAN DEFAULT false);");

        // Create backs table
        sql.append("CREATE TABLE IF NOT EXISTS backs" +
                " (player_id INT NOT NULL," +
                " destination_id INT NOT NULL," +
                " created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

        try {

            connection = database.getConnection();
            statement = connection.createStatement();
            statement.execute(sql.toString());

            this.bumpVersion(DefaultConfig.DATABASE_VERSION, 1);

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }
    }

    public M01_AddInitialDatabaseTables(Destinations plugin) {
        super(plugin);
        this.database = this.plugin.getDatabase();
    }
}
