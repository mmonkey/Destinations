package com.github.mmonkey.Destinations.Events;

import com.github.mmonkey.Destinations.Destinations;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class PlayerBackLocationSaveEvent extends AbstractEvent implements Cancellable {

    private boolean cancelled = false;

    private Player player;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public PlayerBackLocationSaveEvent(Player player) {
        this.player = player;
    }

    public Cause getCause() {
        return Cause.of(Destinations.getInstance());
    }
}
