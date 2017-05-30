package com.github.mmonkey.destinations.events;

import com.flowpowered.math.vector.Vector3d;
import com.github.mmonkey.destinations.configs.DestinationsConfig;
import com.github.mmonkey.destinations.utilities.BlockUtil;
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
    private String locationType;

    /**
     * AbstractPlayerTeleportEvent constructor
     *
     * @param player   Player
     * @param location Location
     * @param rotation Vector3d|null
     */
    AbstractPlayerTeleportEvent(Player player, Location<World> location, Vector3d rotation, String locationType) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(location);

        this.player = player;
        this.location = location;
        this.rotation = rotation;
        this.locationType = locationType;
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

    public String getLocationType() {
        return this.locationType;
    }

    public BigDecimal calculateCost(String locationType) {
        String type = DestinationsConfig.getLocationTypeEconomyType(locationType);
        Double rate = DestinationsConfig.getLocationTypeEconomyRate(locationType);

        if (rate <= 0) {
            return BigDecimal.ZERO;
        }

        if (type.toLowerCase().equals("variable")) {
            double distance = BlockUtil.distance(this.getTargetEntity().getLocation(), this.getLocation());
            return BigDecimal.valueOf(Math.ceil(distance / 100 * rate));
        }

        return BigDecimal.valueOf(rate);
    }

}
