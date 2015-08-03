package com.github.mmonkey.Destinations.Dams;

import com.github.mmonkey.Destinations.Database.Database;

import java.sql.*;
import java.util.UUID;

public class PlayerDam {

    public static final String tblName = "players";

    private Database database;

    public int getPlayerId(UUID playerUniqueId) {

        int playerId = 0;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        String sql = "SELECT id FROM " + tblName + " WHERE unique_id = ? LIMIT 1";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, playerUniqueId.toString());
            result = statement.executeQuery();

            while (result.next()) {
                playerId = result.getInt("id");
            }

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (result != null) result.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        if (playerId == 0) {
            playerId = this.insertPlayer(playerUniqueId);
        }

        return playerId;

    }

    private int insertPlayer(UUID playerUniqueId) {

        int id = 0;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        String sql = "INSERT INTO " + tblName + " (unique_id) VALUES (?)";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, playerUniqueId.toString());
            statement.executeUpdate();
            result = statement.getGeneratedKeys();

            if (result.next()) {
                id = result.getInt(1);
            }

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (result != null) result.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        return id;
    }

    public PlayerDam(Database database) {
        this.database = database;
    }
}
