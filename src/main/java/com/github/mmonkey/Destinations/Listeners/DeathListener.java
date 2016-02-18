package com.github.mmonkey.Destinations.Listeners;

import com.github.mmonkey.Destinations.Dams.BackDam;
import com.github.mmonkey.Destinations.Destinations;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;

public class DeathListener {

    private Destinations plugin;
    private BackDam backDam;

    @Listener
    public void onDeath(DestructEntityEvent event) {

        if (event.getTargetEntity() instanceof Player) {
            backDam.setBack((Player) event.getTargetEntity());
        }

    }

    public DeathListener(Destinations plugin) {
        this.plugin = plugin;
        this.backDam = new BackDam(plugin);
    }

}