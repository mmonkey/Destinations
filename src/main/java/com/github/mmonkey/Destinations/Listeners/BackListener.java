package com.github.mmonkey.Destinations.Listeners;

import com.github.mmonkey.Destinations.Dams.BackDam;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Events.PlayerBackLocationSaveEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;

public class BackListener {

    private BackDam backDam;

    @Listener
    public void onBack(PlayerBackLocationSaveEvent event) {

        if (!event.isCancelled()) {
            Player player = event.getPlayer();
            backDam.setBack(player);
        }

    }

    public BackListener(Destinations plugin) {
        this.backDam = new BackDam(plugin);
    }
}
