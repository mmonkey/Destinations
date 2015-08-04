package com.github.mmonkey.Destinations.Listeners;

import com.github.mmonkey.Destinations.Dams.BackDam;
import com.github.mmonkey.Destinations.Destinations;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerDeathEvent;

public class DeathListener {

    private Destinations plugin;
    private BackDam backDam;

    @Subscribe
    public void onDeath(PlayerDeathEvent event) {

        Player player = event.getUser();
        backDam.setBack(player);

    }

    public DeathListener(Destinations plugin) {
        this.plugin = plugin;
        this.backDam = new BackDam(plugin);
    }

}
