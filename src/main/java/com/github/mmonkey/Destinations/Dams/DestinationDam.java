package com.github.mmonkey.Destinations.Dams;

import com.github.mmonkey.Destinations.Database.Database;
import org.spongepowered.api.entity.player.Player;

import java.sql.*;

public class DestinationDam {

    public static final String tblName = "destinations";

    private Database database;
    private WorldDam worldDam;

    public int saveDestination(Player player) {

        int worldId = this.worldDam.getWorldId(player.getWorld().getUniqueId());

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        int id = 0;
        String sql = "INSERT INTO " + tblName +
                " (world_id, x, y, z, yaw, pitch, roll)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?);";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, worldId);
            statement.setDouble(2, player.getLocation().getX());
            statement.setDouble(3, player.getLocation().getY());
            statement.setDouble(4, player.getLocation().getZ());
            statement.setDouble(5, player.getRotation().getX());
            statement.setDouble(6, player.getRotation().getY());
            statement.setDouble(7, player.getRotation().getZ());
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

    public DestinationDam(Database database) {
        this.database = database;
        this.worldDam = new WorldDam(database);
    }
}
