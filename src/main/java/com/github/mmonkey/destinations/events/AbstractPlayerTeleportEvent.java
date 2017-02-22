package com.github.mmonkey.destinations.events;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Preconditions;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;

abstract class AbstractPlayerTeleportEvent extends AbstractEvent implements TargetPlayerEvent, Cancellable {

    private boolean cancelled = false;
    private final Player player;
    private Location<World> location;
    private Vector3d rotation;
    private BigDecimal cost = BigDecimal.ZERO;

    /**
     * AbstractPlayerTeleportEvent constructor
     *
     * @param player   Player
     * @param location Location
     * @param rotation Vector3d|null
     * @param cost     BigDecimal|null
     */
    AbstractPlayerTeleportEvent(Player player, Location<World> location, Vector3d rotation, BigDecimal cost) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(location);

        this.player = player;
        this.location = location;
        this.rotation = rotation;
        if (cost != null) {
            this.cost = cost;
        }
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public Player getTargetEntity() {
        return this.player;
    }

    @Override
    public Cause getCause() {
        return Cause.builder().owner(this.player).build();
    }

    public Location<World> getLocation() {
        return this.location;
    }

    public void setLocation(Location<World> location) {
        this.location = location;
    }

    public Vector3d getRotation() {
        return this.rotation;
    }

    public void setRotation(Vector3d rotation) {
        this.rotation = rotation;
    }

    public BigDecimal getCost() {
        return this.cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

}
