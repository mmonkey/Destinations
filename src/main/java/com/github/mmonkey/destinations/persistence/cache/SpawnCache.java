package com.github.mmonkey.destinations.persistence.cache;

import com.github.mmonkey.destinations.entities.SpawnEntity;

import java.util.HashSet;
import java.util.Set;

public class SpawnCache {

    public static final SpawnCache instance = new SpawnCache();

    private final Set<SpawnEntity> cache = new HashSet<>();

    /**
     * Get SpawnEntities from the cache
     *
     * @return Set<SpawnEntity>
     */
    public Set<SpawnEntity> get() {
        return cache;
    }

}
