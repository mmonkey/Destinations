package com.github.mmonkey.destinations.utilities;

import com.github.mmonkey.destinations.Destinations;
import com.github.mmonkey.destinations.comparators.BedComparator;
import com.github.mmonkey.destinations.entities.BedEntity;
import com.github.mmonkey.destinations.entities.HomeEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.entities.WarpEntity;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.persistence.cache.WarpCache;
import com.github.mmonkey.destinations.persistence.repositories.PlayerRepository;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

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
     * Get the last used bed for this player in the current world
     * If the last used bed has been deleted, or is currently occupied, get the next last bed (and so on)
     *
     * @param playerEntity PlayerEntity
     * @param player       Player
     * @return BedEntity|null
     */
    public static BedEntity getBed(PlayerEntity playerEntity, Player player) {
        List<BedEntity> beds = new CopyOnWriteArrayList<>(playerEntity.getBeds());
        beds.sort(new BedComparator());

        Destinations.getInstance().getLogger().info("Beds: " + beds.size());

        for (BedEntity bed : beds) {
            if (bed.getLocation().getWorld().getIdentifier().equals(player.getWorld().getUniqueId().toString())) {
                Location<World> block = player.getWorld().getLocation(bed.getLocation().getLocation().getBlockPosition());
                if (BlockUtil.isBed(block)) {
                    if (!BlockUtil.isBedOccupied(block)) {
                        return bed;
                    }
                } else {
                    playerEntity.getBeds().remove(bed);
                    playerEntity = PlayerRepository.instance.save(playerEntity);
                    PlayerCache.instance.set(player, playerEntity);
                }
            }
        }
        return null;
    }

    /**
     * Get a list of Player's homes from the world that player is currently in
     *
     * @param playerEntity PlayerEntity
     * @param location     Location
     * @return Set<HomeEntity>
     */
    public static Set<HomeEntity> getFilteredHomes(PlayerEntity playerEntity, Location location) {
        Set<HomeEntity> filtered = new HashSet<>();
        playerEntity.getHomes().forEach(home -> {
            if (home.getLocation().getWorld().getIdentifier().equals(location.getExtent().getUniqueId().toString())) {
                filtered.add(home);
            }
        });
        return filtered;
    }

    /**
     * Get a player's home by name
     *
     * @param playerEntity PlayerEntity
     * @param name         String
     * @return HomeEntity
     */
    public static HomeEntity getPlayerHomeByName(PlayerEntity playerEntity, String name) {
        for (HomeEntity home : playerEntity.getHomes()) {
            if (home.getName().equalsIgnoreCase(name)) {
                return home;
            }
        }
        return null;
    }

    /**
     * Get the next available name for a player's home
     *
     * @param playerEntity PlayerEntity
     * @return String
     */
    public static String getPlayerHomeAvailableName(PlayerEntity playerEntity) {
        int max = 0, temp = 0;
        for (HomeEntity home : playerEntity.getHomes()) {
            if (home.getName().startsWith("home") && home.getName().matches(".*\\d.*")) {
                temp = Integer.parseInt(home.getName().replaceAll("[\\D]", ""));
            }
            if (temp > max) {
                max = temp;
            }
        }
        if (playerEntity.getHomes().size() > max) {
            max = playerEntity.getHomes().size();
        }
        return (playerEntity.getHomes().size() == 0) ? "home" : "home" + Integer.toString(max + 1);
    }

    /**
     * Get a list of warps that this player can use
     *
     * @param playerEntity PlayerEntity
     * @return Set<WarpEntity>
     */
    public static Set<WarpEntity> getPlayerWarps(PlayerEntity playerEntity) {
        Set<WarpEntity> results = new HashSet<>();
        WarpCache.instance.get().forEach(warp -> {
            if (!warp.isPrivate()) {
                results.add(warp);
            } else if (warp.getOwner().getIdentifier().equals(playerEntity.getIdentifier())) {
                results.add(warp);
            } else {
                warp.getAccess().forEach(access -> {
                    if (access.getPlayer().getIdentifier().equals(playerEntity.getIdentifier())) {
                        results.add(warp);
                    }
                });
            }
        });
        return results;
    }

}
