package com.github.mmonkey.destinations.events.interfaces;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;

public interface PlayerTeleportEvent extends TargetPlayerEvent, Cancellable {

    Location<World> getLocation();

    void setLocation(Location<World> location);

    Vector3d getRotation();

    void setRotation(Vector3d rotation);

    String getLocationType();

    BigDecimal calculateCost(String locationType);

    /**
     * Called before a player is teleported.
     * This event is used to save the Player's current location as their back location.
     */
    interface Pre extends PlayerTeleportEvent {
    }

    /**
     * Called when a player is teleported to a back location.
     */
    interface Back extends PlayerTeleportEvent {
    }

    /**
     * Called when a player is teleported to a bed location.
     */
    interface Bed extends PlayerTeleportEvent {
    }

    /**
     * Called when a player is teleported to a bring location.
     */
    interface Bring extends PlayerTeleportEvent {
    }

    /**
     * Called when a player is teleported to a grab location.
     */
    interface Grab extends PlayerTeleportEvent {
    }

    /**
     * Called when a player is teleported to a home location.
     */
    interface Home extends PlayerTeleportEvent {
    }

    /**
     * Called when a player is teleported to a jump location.
     */
    interface Jump extends PlayerTeleportEvent {
    }

    /**
     * Called when a player is teleported to a spawn location.
     */
    interface Spawn extends PlayerTeleportEvent {
    }

    /**
     * Called when a player is teleported to a top location.
     */
    interface Top extends PlayerTeleportEvent {
    }

    /**
     * Called when a player is teleported to a warp location.
     */
    interface Warp extends PlayerTeleportEvent {
    }

}
