package com.github.mmonkey.destinations.utilities;

import com.github.mmonkey.destinations.entities.HomeEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.entities.WarpEntity;
import com.github.mmonkey.destinations.persistence.repositories.PlayerRepository;
import com.github.mmonkey.destinations.persistence.repositories.WarpRepository;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PlayerUtil {

    /**
     * Get the PlayerEntity in storage for this player
     *
     * @param player Player
     * @return PlayerEntity
     */
    public static PlayerEntity getPlayerEntity(Player player) {
        Optional<PlayerEntity> optional = PlayerRepository.instance.get(player);
        return optional.orElseGet(() -> PlayerRepository.instance.save(new PlayerEntity(player)));
    }

    /**
     * Get a list of Player's homes from the world that player is currently in
     *
     * @param player   PlayerEntity
     * @param location Location
     * @return Set<HomeEntity>
     */
    public static Set<HomeEntity> getFilteredHomes(PlayerEntity player, Location location) {
        Set<HomeEntity> filtered = new HashSet<>();
        player.getHomes().forEach(home -> {
            if (home.getLocation().getWorld().getIdentifier().equals(location.getExtent().getUniqueId().toString())) {
                filtered.add(home);
            }
        });
        return filtered;
    }

    /**
     * Get a Player's home by name
     *
     * @param player PlayerEntity
     * @param name   String
     * @return HomeEntity
     */
    public static HomeEntity getPlayerHomeByName(PlayerEntity player, String name) {
        for (HomeEntity home : player.getHomes()) {
            if (home.getName().equalsIgnoreCase(name)) {
                return home;
            }
        }
        return null;
    }

    /**
     * Get the next available name for a player's home
     *
     * @param player PlayerEntity
     * @return String
     */
    public static String getPlayerHomeAvailableName(PlayerEntity player) {
        int max = 0, temp = 0;
        for (HomeEntity home : player.getHomes()) {
            if (home.getName().startsWith("home") && home.getName().matches(".*\\d.*")) {
                temp = Integer.parseInt(home.getName().replaceAll("[\\D]", ""));
            }
            if (temp > max) {
                max = temp;
            }
        }
        if (player.getHomes().size() > max) {
            max = player.getHomes().size();
        }
        return (player.getHomes().size() == 0) ? "home" : "home" + Integer.toString(max + 1);
    }

    /**
     * Get a list of warps that this player can use
     *
     * @param player PlayerEntity
     * @return Set<WarpEntity>
     */
    public static Set<WarpEntity> getPlayerWarps(PlayerEntity player) {
        Set<WarpEntity> results = new HashSet<>();
        List<WarpEntity> warps = WarpRepository.instance.getAllWarps();
        warps.forEach(warp -> {
            if (!warp.isPrivate()) {
                results.add(warp);
            } else if (warp.getOwner().getIdentifier().equals(player.getIdentifier())) {
                results.add(warp);
            } else {
                warp.getAccess().forEach(access -> {
                    if (access.getPlayer().getIdentifier().equals(player.getIdentifier())) {
                        results.add(warp);
                    }
                });
            }
        });
        return results;
    }

}
