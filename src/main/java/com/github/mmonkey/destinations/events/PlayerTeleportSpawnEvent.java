package com.github.mmonkey.destinations.events;

import com.flowpowered.math.vector.Vector3d;
import com.github.mmonkey.destinations.events.interfaces.PlayerTeleportEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlayerTeleportSpawnEvent extends AbstractPlayerTeleportEvent implements PlayerTeleportEvent.Spawn {

    public static final String LOCATION_TYPE = "spawn";

    /**
     * PlayerTeleportSpawnEvent constructor
     *
     * @param player   Player
     * @param location Location
     * @param rotation Vector3d|null
     */
    public PlayerTeleportSpawnEvent(Player player, Location<World> location, Vector3d rotation) {
        super(player, location, rotation, LOCATION_TYPE);
    }

}
