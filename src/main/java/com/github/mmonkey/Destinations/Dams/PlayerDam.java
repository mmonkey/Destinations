package com.github.mmonkey.Destinations.Dams;

import com.github.mmonkey.Destinations.Database.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class PlayerDam {

    public static final String tblName = "players";

    private Database database;

    public int getPlayerId(UUID playerUniqueId) {

        this.addPlayer(playerUniqueId);

        int playerId = 0;
        Connection connection = null;
        Statement statement = null;
        ResultSet result;
        String sql = "SELECT id FROM " + tblName +
                " WHERE unique_id = " + playerUniqueId.toString() +
                " LIMIT 1;";

        try {

            connection = database.getConnection();
            statement = connection.createStatement();
            result = statement.executeQuery(sql);

            while (result.next()) {
                playerId = result.getInt("id");
            }

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        return playerId;

    }

    private void addPlayer(UUID playerUniqueId) {

        Connection connection = null;
        Statement statement = null;
        String sql = "IF NOT EXISTS " +
                "(SELECT id FROM " + tblName +
                " WHERE unique_id = " + playerUniqueId.toString() + ")" +
                " INSERT INTO " + tblName + " (unique_id)" +
                " VALUES (" + playerUniqueId.toString() + ");";

        try {

            connection = database.getConnection();
            statement = connection.createStatement();
            statement.execute(sql);

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }
    }

    public PlayerDam(Database database) {
        this.database = database;
    }
}
