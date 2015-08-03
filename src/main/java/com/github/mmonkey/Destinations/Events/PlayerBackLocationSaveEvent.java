package com.github.mmonkey.Destinations.Events;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.AbstractEvent;
import org.spongepowered.api.event.Cancellable;

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

}
