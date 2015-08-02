package com.github.mmonkey.Destinations.Dams;

import com.github.mmonkey.Destinations.Database.Database;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Models.DestinationModel;
import com.github.mmonkey.Destinations.Models.HomeModel;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.World;

import javax.print.attribute.standard.Destination;
import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class HomeDam {

    public static final String tblName = "homes";

    private Destinations plugin;
    private Database database;
    private PlayerDam playerDam;
    private DestinationDam destinationDam;

    public ArrayList<HomeModel> getPlayerHomes(Player player) {

        int playerId = this.playerDam.getPlayerId(player.getUniqueId());

        ArrayList<HomeModel> homes = new ArrayList<HomeModel>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        String sql = "SELECT" +
                " homes.id, " +
                " homes.name," +
                " homes.owner_id," +
                " destinations.id," +
                " destinations.x," +
                " destinations.y," +
                " destinations.z," +
                " destinations.yaw," +
                " destinations.pitch," +
                " destinations.roll," +
                " worlds.unique_id" +
                " FROM " + tblName +
                " JOIN destinations ON homes.destination_id = destinations.id" +
                " JOIN worlds ON destinations.world_id = worlds.id" +
                " JOIN players ON homes.owner_id = players.id" +
                " WHERE homes.owner_id = ?";

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

                HomeModel home = new HomeModel(
                        result.getInt("homes.id"),
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

        return getFilteredHomes(homes);

    }

    public HomeModel getPlayerHomeByName(Player player, String name) {

        int playerId = this.playerDam.getPlayerId(player.getUniqueId());

        HomeModel home = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        String sql = "SELECT" +
                " homes.id," +
                " homes.name," +
                " homes.owner_id," +
                " destinations.id," +
                " destinations.x," +
                " destinations.y," +
                " destinations.z," +
                " destinations.yaw," +
                " destinations.pitch," +
                " destinations.roll," +
                " worlds.unique_id" +
                " FROM " + tblName +
                " JOIN destinations ON homes.destination_id = destinations.id" +
                " JOIN worlds ON destinations.world_id = worlds.id" +
                " JOIN players ON homes.owner_id = players.id" +
                " WHERE homes.owner_id = ?" +
                " AND UPPER(homes.name) = UPPER(?)" +
                " LIMIT 1";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, playerId);
            statement.setString(2, name);
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

                home = new HomeModel(
                        result.getInt("homes.id"),
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

        return this.isHomeInGameWorlds(home) ? home : null;
    }

    public HomeModel insertHome(Player player, String name) {

        HomeModel home = null;
        int id = 0;
        int playerId = this.playerDam.getPlayerId(player.getUniqueId());
        DestinationModel destination = this.destinationDam.insertDestination(player);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        String sql = "INSERT INTO " + tblName +
                " (destination_id, owner_id, name)" +
                " VALUES (?, ?, ?)";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, destination.getId());
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

        if (id > 0) {
            home = new HomeModel(id, name, destination);
        }

        return home;
    }

    public HomeModel updateHome(Player player, HomeModel home) {

        Connection connection = null;
        PreparedStatement statement = null;

        int updatedRows = 0;

        destinationDam.deleteDestination(home.getDestination());
        DestinationModel destination = destinationDam.insertDestination(player);
        String sql = "UPDATE " + tblName + " SET destination_id = ? WHERE id = ?";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, destination.getId());
            statement.setInt(2, home.getId());
            updatedRows = statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        if (updatedRows > 0) {
            home.setDestination(destination);
        }

        return home;
    }

    public HomeModel updateHome(HomeModel home, String newName) {

        Connection connection = null;
        PreparedStatement statement = null;

        int updatedRows = 0;

        String sql = "UPDATE " + tblName + " SET name = ? WHERE id = ?";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, newName);
            statement.setInt(2, home.getId());
            updatedRows = statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        if (updatedRows > 0) {
            home.setName(newName);
        }

        return home;
    }

    public boolean deleteHome(Player player, HomeModel home) {

        int playerId = this.playerDam.getPlayerId(player.getUniqueId());
        this.destinationDam.deleteDestination(home.getDestination());

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        String sql = "DELETE FROM " + tblName +
                " WHERE owner_id = ?" +
                " AND destination_id = ?" +
                " AND UPPER(homes.name) = UPPER(?)" +
                " LIMIT 1";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, playerId);
            statement.setInt(2, home.getDestination().getId());
            statement.setString(3, home.getName());
            statement.executeUpdate();
            result = statement.getGeneratedKeys();
            return true;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;

        } finally {

            try { if (result != null) result.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

    }

    private ArrayList<HomeModel> getFilteredHomes(ArrayList<HomeModel> homes) {

        ArrayList<HomeModel> filteredHomes = new ArrayList<HomeModel>();
        Collection<World> worlds = plugin.getGame().getServer().getWorlds();

        for (World world : worlds) {
            for (HomeModel home : homes) {
                if (home.getDestination().getWorldUniqueId().equals(world.getUniqueId())) {
                    filteredHomes.add(home);
                }
            }
        }

        return filteredHomes;
    }

    private boolean isHomeInGameWorlds(HomeModel home) {

        if (home == null) {
            return false;
        }

        Collection<World> worlds = plugin.getGame().getServer().getWorlds();

        for (World world : worlds) {
            if (home.getDestination().getWorldUniqueId().equals(world.getUniqueId())) {
                return true;
            }
        }

        return false;
    }

    public HomeDam(Destinations plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
        this.playerDam = new PlayerDam(plugin.getDatabase());
        this.destinationDam = new DestinationDam(plugin.getDatabase());
    }


}
