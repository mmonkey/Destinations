package com.github.mmonkey.Destinations.Dams;

import com.github.mmonkey.Destinations.Database.Database;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Models.DestinationModel;
import com.github.mmonkey.Destinations.Models.WarpModel;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.World;

import java.sql.*;
import java.util.*;

public class WarpDam {

    public static final String tblName = "warps";

    private Destinations plugin;
    private Database database;
    private PlayerDam playerDam;
    private DestinationDam destinationDam;

    public ArrayList<WarpModel> getAllWarps() {

        ArrayList<WarpModel> warps = new ArrayList<WarpModel>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        String sql = "SELECT" +
                " warps.id, " +
                " warps.name," +
                " players.unique_id," +
                " warps.is_public," +
                " destinations.id," +
                " destinations.x," +
                " destinations.y," +
                " destinations.z," +
                " destinations.yaw," +
                " destinations.pitch," +
                " destinations.roll," +
                " worlds.unique_id," +
                " FROM " + tblName +
                " JOIN players ON warps.owner_id = players.id" +
                " JOIN destinations ON warps.destination_id = destinations.id" +
                " JOIN worlds ON destinations.world_id = worlds.id";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql);
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

                WarpModel warp = new WarpModel(
                        result.getInt("warps.id"),
                        result.getString("warps.name"),
                        UUID.fromString(result.getString("players.unique_id")),
                        destination,
                        result.getBoolean("warps.is_public")
                );

                warps.add(getWhitelist(warp));
            }

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (result != null) result.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        return getFilteredWarps(warps);

    }

    public ArrayList<WarpModel> getPlayerWarps(Player player) {

        ArrayList<WarpModel> playerWarps = new ArrayList<WarpModel>();
        ArrayList<WarpModel> warps = this.getAllWarps();
        for (WarpModel warp : warps) {
            if (warp.isPublic() || warp.getWhitelist().containsKey(player.getUniqueId())) {
                playerWarps.add(warp);
            }
        }

        return playerWarps;
    }

    public WarpModel insertWarp(Player player, String name, Boolean isPublic) {

        WarpModel warp = null;
        int id = 0;
        int playerId = this.playerDam.getPlayerId(player.getUniqueId());
        DestinationModel destination = this.destinationDam.insertDestination(player);

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        String sql = "INSERT INTO " + tblName +
                " (destination_id, owner_id, name, is_public)" +
                " VALUES (?, ?, ?, ?)";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, destination.getId());
            statement.setInt(2, playerId);
            statement.setString(3, name);
            statement.setBoolean(4, isPublic);
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
            warp = new WarpModel(id, name, player.getUniqueId(), destination, isPublic);
        }

        return warp;
    }

    public WarpModel updateWarp(Player player, WarpModel warp) {

        this.updateWhitelist(warp);

        Connection connection = null;
        PreparedStatement statement = null;

        int updatedRows = 0;

        destinationDam.deleteDestination(warp.getDestination());
        DestinationModel destination = destinationDam.insertDestination(player);
        String sql = "UPDATE " + tblName + " SET destination_id = ? WHERE id = ?";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, destination.getId());
            statement.setInt(2, warp.getId());
            updatedRows = statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        if (updatedRows > 0) {
            warp.setDestination(destination);
        }

        return warp;
    }

    public boolean deleteWarp(WarpModel warp) {

        int deleted = 0;

        this.destinationDam.deleteDestination(warp.getDestination());
        this.deleteWhitelist(warp);

        Connection connection = null;
        PreparedStatement statement = null;

        String sql = "DELETE FROM " + tblName + " WHERE id = ?";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, warp.getId());
            deleted = statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        return (deleted > 0);
    }

    private WarpModel getWhitelist(WarpModel warp) {

        HashMap<UUID, Boolean> whitelist = new HashMap<UUID, Boolean>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        String sql = "SELECT " +
                " warp_players.can_edit" +
                " players.unique_id" +
                " FROM warp_players" +
                " JOIN players ON players.id = warp_players.player_id" +
                " WHERE warp_players.warp_id = ?";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, warp.getId());
            result = statement.executeQuery();

            while (result.next()) {

                whitelist.put(UUID.fromString(
                                result.getString("players.unique_id")),
                        result.getBoolean("warp_players.can_edit")
                );

            }

            warp.setWhitelist(whitelist);

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (result != null) result.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        return warp;
    }

    private boolean insertWhitelist(WarpModel warp) {

        int inserted = 0;
        Connection connection = null;
        PreparedStatement statement = null;

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO warp_players (warp_id, player_id, can_edit) VALUES");

        int i = 0;
        for (Map.Entry<UUID, Boolean> item : warp.getWhitelist().entrySet()) {

            int playerId = this.playerDam.getPlayerId(item.getKey());
            sql.append((i == 0) ? " " : ", ")
                    .append("(")
                    .append(warp.getId())
                    .append(", ")
                    .append(playerId)
                    .append(", ")
                    .append(item.getValue())
                    .append(")");
            i++;

        }

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql.toString());
            inserted = statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        return (inserted > 0);
    }

    private boolean updateWhitelist(WarpModel warp) {

        return (this.deleteWhitelist(warp) && this.insertWhitelist(warp));
    }

    private boolean deleteWhitelist(WarpModel warp) {

        this.destinationDam.deleteDestination(warp.getDestination());

        int deleted = 0;
        Connection connection = null;
        PreparedStatement statement = null;

        String sql = "DELETE FROM warp_players WHERE warp_id = ?";

        try {

            connection = database.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, warp.getId());
            deleted = statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }

        }

        return (deleted > 0);
    }

    private ArrayList<WarpModel> getFilteredWarps(ArrayList<WarpModel> warps) {

        ArrayList<WarpModel> filteredWarps = new ArrayList<WarpModel>();
        Collection<World> worlds = plugin.getGame().getServer().getWorlds();

        for (World world : worlds) {
            for (WarpModel warp : warps) {
                if (warp.getDestination().getWorldUniqueId().equals(world.getUniqueId())) {
                    filteredWarps.add(warp);
                }
            }
        }

        return filteredWarps;
    }

    public WarpDam(Destinations plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
        this.playerDam = new PlayerDam(plugin.getDatabase());
        this.destinationDam = new DestinationDam(plugin.getDatabase());
    }

}
