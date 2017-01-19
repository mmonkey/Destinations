package com.github.mmonkey.destinations.events;

import com.flowpowered.math.vector.Vector3d;
import com.github.mmonkey.destinations.events.interfaces.PlayerTeleportEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlayerTeleportJumpEvent extends AbstractPlayerTeleportEvent implements PlayerTeleportEvent.Jump {

    /**
     * PlayerTeleportJumpEvent constructor
     *
     * @param player   Player
     * @param location Location
     * @param rotation Vector3d
     */
    public PlayerTeleportJumpEvent(Player player, Location<World> location, Vector3d rotation) {
        super(player, location, rotation);
    }
}
