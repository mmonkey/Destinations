package com.github.mmonkey.destinations.persistence.cache;

import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.utilities.PlayerUtil;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerCache {

    public static final PlayerCache instance = new PlayerCache();

    private final Map<Player, PlayerEntity> cache = new ConcurrentHashMap<>();
    private final Map<Player, ResourceBundle> resourceCache = new ConcurrentHashMap<>();

    /**
     * Get the PlayerEntity for this Player from the cache
     *
     * @param player Player
     * @return PlayerEntity
     */
    public PlayerEntity get(Player player) {
        if (!cache.containsKey(player)) {
            PlayerEntity playerEntity = PlayerUtil.getPlayerEntity(player);
            cache.put(player, playerEntity);
        }
        return cache.get(player);
    }

    /**
     * Set the PlayerEntity for this Player in the cache
     *
     * @param player       Player
     * @param playerEntity PlayerEntity
     */
    public void set(Player player, PlayerEntity playerEntity) {
        cache.put(player, playerEntity);
    }

    /**
     * Get the ResourceBundle for this player from the cache
     *
     * @param player Player
     * @return ResourceBundle
     */
    public ResourceBundle getResourceCache(Player player) {
        if (!resourceCache.containsKey(player)) {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/messages", player.getLocale());
            resourceCache.put(player, resourceBundle);
        }
        return resourceCache.get(player);
    }

}
