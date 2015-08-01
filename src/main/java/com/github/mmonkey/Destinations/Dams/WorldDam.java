package com.github.mmonkey.Destinations.Dams;

import com.github.mmonkey.Destinations.Database.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class WorldDam {

    public static final String tblName = "worlds";

    private Database database;

    public int getWorldId(UUID worldUniqueId) {

        this.addWorld(worldUniqueId);

        int worldId = 0;
        Connection connection = null;
        Statement statement = null;
        ResultSet result;
        String sql = "SELECT id FROM " + tblName +
                " WHERE unique_id = " + worldUniqueId.toString() +
                " LIMIT 1;";

        try {

            connection = database.getConnection();
            statement = connection.createStatement();
            result = statement.executeQuery(sql);

            while (result.next()) {
                worldId = result.getInt("id");
            }

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        return worldId;

    }

    private void addWorld(UUID worldUniqueId) {

        Connection connection = null;
        Statement statement = null;
        String sql = "IF NOT EXISTS " +
                "(SELECT id FROM " + tblName +
                " WHERE unique_id = " + worldUniqueId.toString() + ")" +
                " INSERT INTO " + tblName + " (unique_id)" +
                " VALUES (" + worldUniqueId.toString() + ");";

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

    public WorldDam(Database database) {
        this.database = database;
    }
}
