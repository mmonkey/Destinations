package com.github.mmonkey.destinations.events;

import com.flowpowered.math.vector.Vector3d;
import com.github.mmonkey.destinations.events.interfaces.PlayerTeleportEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlayerTeleportWarpEvent extends AbstractPlayerTeleportEvent implements PlayerTeleportEvent.Warp {

    public static final String LOCATION_TYPE = "warp";

    /**
     * PlayerTeleportWarpEvent constructor
     *
     * @param player   Player
     * @param location Location
     * @param rotation Vector3d|null
     */
    public PlayerTeleportWarpEvent(Player player, Location<World> location, Vector3d rotation) {
        super(player, location, rotation, LOCATION_TYPE);
    }

}
