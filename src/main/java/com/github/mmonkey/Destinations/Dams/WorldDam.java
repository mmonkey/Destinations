package com.github.mmonkey.Destinations.Dams;

import com.github.mmonkey.Destinations.Database.Database;

import java.sql.*;
import java.util.UUID;

public class WorldDam {

    public static final String tblName = "worlds";

    private Database database;

    public int getWorldId(UUID worldUniqueId) {

        int worldId = 0;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        String sql = "SELECT id FROM " + tblName + " WHERE unique_id = ? LIMIT 1";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, worldUniqueId.toString());
            result = statement.executeQuery();

            while (result.next()) {
                worldId = result.getInt("id");
            }

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (result != null) result.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        if (worldId == 0) {
            worldId = this.insertWorld(worldUniqueId);
        }

        return worldId;

    }

    private int insertWorld(UUID worldUniqueId) {

        int id = 0;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        String sql = "INSERT INTO " + tblName + " (unique_id) VALUES (?)";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, worldUniqueId.toString());
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

    public WorldDam(Database database) {
        this.database = database;
    }
}
