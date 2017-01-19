package com.github.mmonkey.destinations.persistence.cache;

import com.github.mmonkey.destinations.entities.WarpEntity;

import java.util.HashSet;
import java.util.Set;

public class WarpCache {

    public static final WarpCache instance = new WarpCache();

    private final Set<WarpEntity> cache = new HashSet<>();

    /**
     * Get WarpEntities from the cache
     *
     * @return Set<WarpEntity>
     */
    public Set<WarpEntity> get() {
        return cache;
    }

}
