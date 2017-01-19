package com.github.mmonkey.destinations.listeners;

import com.github.mmonkey.destinations.entities.BackEntity;
import com.github.mmonkey.destinations.entities.LocationEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.events.interfaces.PlayerTeleportEvent;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.persistence.repositories.PlayerRepository;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.filter.type.Include;
import org.spongepowered.api.util.Tristate;

public class TeleportListeners {

    @Listener(order = Order.LATE)
    @IsCancelled(Tristate.FALSE)
    @Include(PlayerTeleportEvent.Pre.class)
    public void onPlayerTeleportPreEvent(PlayerTeleportEvent event) {
        Player player = event.getTargetEntity();
        PlayerEntity playerEntity = PlayerCache.instance.get(player);

        for (BackEntity backEntity : playerEntity.getBacks()) {
            if (backEntity.getLocation().getWorld().getIdentifier().equals(event.getLocation().getExtent().getUniqueId().toString())) {
                playerEntity.getBacks().remove(backEntity);
            }
        }

        playerEntity.getBacks().add(new BackEntity(new LocationEntity(event.getLocation(), event.getRotation())));
        playerEntity = PlayerRepository.instance.save(playerEntity);
        PlayerCache.instance.set(player, playerEntity);
    }

    @Listener(order = Order.LATE)
    @IsCancelled(Tristate.FALSE)
    @Exclude(PlayerTeleportEvent.Pre.class)
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        if (event.getRotation() == null) {
            event.getTargetEntity().setLocationSafely(event.getLocation());
        } else {
            event.getTargetEntity().setLocationAndRotationSafely(event.getLocation(), event.getRotation());
        }
    }

}
