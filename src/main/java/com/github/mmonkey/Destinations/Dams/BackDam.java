package com.github.mmonkey.Destinations.Dams;

import com.github.mmonkey.Destinations.Database.Database;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Models.BackModel;
import com.github.mmonkey.Destinations.Models.DestinationModel;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public class BackDam {

    public static final String tblName = "backs";

    private Destinations plugin;
    private Database database;
    private PlayerDam playerDam;
    private DestinationDam destinationDam;

    public BackModel getBack(Player player) {

        ArrayList<BackModel> backs = new ArrayList<BackModel>();
        int playerId = this.playerDam.getPlayerId(player.getUniqueId());

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        String sql = "SELECT" +
                " backs.created_on," +
                " worlds.unique_id," +
                " destinations.id," +
                " destinations.x," +
                " destinations.y," +
                " destinations.z," +
                " destinations.yaw," +
                " destinations.pitch," +
                " destinations.roll" +
                " FROM " + tblName +
                " JOIN destinations ON backs.destination_id = destinations.id" +
                " JOIN worlds ON destinations.world_id = worlds.id" +
                " WHERE backs.player_id = ?";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, playerId);
            result = statement.executeQuery();

            while (result.next()) {

                DestinationModel destination = new DestinationModel(
                        result.getInt("destinations.id"),
                        UUID.fromString(result.getString("worlds.unique_id")),
                        result.getDouble("destinations.x"),
                        result.getDouble("destinations.y"),
                        result.getDouble("destinations.z"),
                        result.getDouble("destinations.yaw"),
                        result.getDouble("destinations.pitch"),
                        result.getDouble("destinations.roll")
                );

                BackModel back = new BackModel(
                        destination,
                        result.getTimestamp("backs.created_on")
                );

                backs.add(back);
            }

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (result != null) result.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        return fiterGameWorlds(backs);
    }

    public BackModel setBack(Player player) {

        this.deleteBack(player);

        int id = 0;
        int playerId = this.playerDam.getPlayerId(player.getUniqueId());
        DestinationModel destination = this.destinationDam.insertDestination(player);
        BackModel back = null;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        Timestamp now = new Timestamp(new Date().getTime());
        String sql = "INSERT INTO " + tblName + " (player_id, destination_id, created_on) VALUES (?, ?, ?)";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, playerId);
            statement.setInt(2, destination.getId());
            statement.setTimestamp(3, now);
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

        if (id > 0) {
            back = new BackModel(destination, now);
        }

        return back;
    }

    private boolean deleteBack(Player player) {

        int deleted = 0;
        int playerId = this.playerDam.getPlayerId(player.getUniqueId());
        int destinationId = 0;

        BackModel back = this.getBack(player);
        if (back != null) {
            destinationId = back.getDestination().getId();
            destinationDam.deleteDestination(back.getDestination());
        }

        Connection connection = null;
        PreparedStatement statement = null;

        String sql = "DELETE FROM " + tblName + " WHERE player_id = ? AND destination_id = ?";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, playerId);
            statement.setInt(2, destinationId);
            deleted = statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        return  (deleted > 0);
    }

    private BackModel fiterGameWorlds(ArrayList<BackModel> backs) {

        Collection<World> worlds = plugin.getGame().getServer().getWorlds();

        for (World world : worlds) {
            for (BackModel back : backs) {
                if (back.getDestination().getWorldUniqueId().equals(world.getUniqueId())) {
                    return back;
                }
            }
        }

        return null;
    }

    public BackDam(Destinations plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
        this.playerDam = new PlayerDam(plugin.getDatabase());
        this.destinationDam = new DestinationDam(plugin.getDatabase());
    }

}
