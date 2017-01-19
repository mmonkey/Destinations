package com.github.mmonkey.destinations.persistence.cache;

import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.utilities.PlayerUtil;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerCache {

    public static final PlayerCache instance = new PlayerCache();

    private final Map<Player, PlayerEntity> cache = new ConcurrentHashMap<>();

    /**
     * Get the PlayerEntity for this Player from the cache
     *
     * @param player Player
     * @return PlayerEntity
     */
    public PlayerEntity get(Player player) {
        if (cache.containsKey(player)) {
            return cache.get(player);
        } else {
            PlayerEntity playerEntity = PlayerUtil.getPlayerEntity(player);
            cache.put(player, playerEntity);
            return playerEntity;
        }
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

}
