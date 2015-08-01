package com.github.mmonkey.Destinations.Dams;

import com.github.mmonkey.Destinations.Database.Database;
import com.github.mmonkey.Destinations.Models.DestinationModel;
import com.github.mmonkey.Destinations.Models.HomeModel;
import org.spongepowered.api.entity.player.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class HomeDam {

    public static final String tblName = "homes";

    private Database database;
    private PlayerDam playerDam;
    private DestinationDam destinationDam;

    String sql = "SELECT" +
            " homes.name," +
            " homes.owner_id," +
            " destinations.x," +
            " destinations.y," +
            " destinations.z," +
            " destinations.yaw," +
            " destinations.pitch," +
            " destinations.roll," +
            " world.unique_id" +
            " FROM " + tblName +
            " JOIN destinations ON homes.destination_id = destinations.id" +
            " JOIN worlds ON destinations.world_id = worlds.id" +
            " JOIN players ON homes.owner_id = players.id;";

    public ArrayList<HomeModel> getPlayerHomes(Player player) {

        ArrayList<HomeModel> homes = new ArrayList<HomeModel>();
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        String sql = "SELECT" +
                " homes.name," +
                " homes.owner_id," +
                " destinations.x," +
                " destinations.y," +
                " destinations.z," +
                " destinations.yaw," +
                " destinations.pitch," +
                " destinations.roll," +
                " world.unique_id" +
                " FROM " + tblName +
                " JOIN destinations ON homes.destination_id = destinations.id" +
                " JOIN worlds ON destinations.world_id = worlds.id" +
                " JOIN players ON homes.owner_id = players.id" +
                " WHERE homes.owner_id = " + player.getUniqueId().toString() + ";";

        try {

            connection = database.getConnection();
            statement = connection.createStatement();
            result = statement.executeQuery(sql);

            while (result.next()) {

                DestinationModel destination = new DestinationModel(
                        UUID.fromString(result.getString("world.unique_id")),
                        result.getDouble("destinations.x"),
                        result.getDouble("destinations.y"),
                        result.getDouble("destinations.z"),
                        result.getDouble("destinations.yaw"),
                        result.getDouble("destinations.pitch"),
                        result.getDouble("destinations.roll")
                );

                HomeModel home = new HomeModel(
                        result.getString("homes.name"),
                        destination
                );

                homes.add(home);
            }

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (result != null) result.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        return homes;

    }

    public HomeModel getPlayerHomeByName(Player player, String name) {

        HomeModel home = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        String sql = "SELECT" +
                " homes.name," +
                " homes.owner_id," +
                " destinations.x," +
                " destinations.y," +
                " destinations.z," +
                " destinations.yaw," +
                " destinations.pitch," +
                " destinations.roll," +
                " world.unique_id" +
                " FROM " + tblName +
                " JOIN destinations ON homes.destination_id = destinations.id" +
                " JOIN worlds ON destinations.world_id = worlds.id" +
                " JOIN players ON homes.owner_id = players.id" +
                " WHERE homes.owner_id = " + player.getUniqueId().toString() +
                " AND UPPER(homes.name) = UPPER(" + name + ")" +
                " LIMIT 1;";

        try {

            connection = database.getConnection();
            statement = connection.createStatement();
            result = statement.executeQuery(sql);

            while (result.next()) {

                DestinationModel destination = new DestinationModel(
                        UUID.fromString(result.getString("world.unique_id")),
                        result.getDouble("destinations.x"),
                        result.getDouble("destinations.y"),
                        result.getDouble("destinations.z"),
                        result.getDouble("destinations.yaw"),
                        result.getDouble("destinations.pitch"),
                        result.getDouble("destinations.roll")
                );

                home = new HomeModel(
                        result.getString("homes.name"),
                        destination
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (result != null) result.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        return home;
    }

    public int saveHome(Player player, String name) {

        int playerId = this.playerDam.getPlayerId(player.getUniqueId());
        int destinationId = this.destinationDam.saveDestination(player);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        int id = 0;
        String sql = "INSERT INTO " + tblName +
                " (destination_id, owner_id, name)" +
                " VALUES (?, ?, ?);";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, destinationId);
            statement.setInt(2, playerId);
            statement.setString(3, name);
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

    public HomeDam(Database database) {
        this.database = database;
        this.playerDam = new PlayerDam(database);
        this.destinationDam = new DestinationDam(database);
    }


}
