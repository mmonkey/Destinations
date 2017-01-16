package com.github.mmonkey.destinations.listeners;

import com.github.mmonkey.destinations.configs.DestinationsConfig;
import com.github.mmonkey.destinations.entities.BackEntity;
import com.github.mmonkey.destinations.entities.LocationEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.events.PlayerBackLocationSaveEvent;
import com.github.mmonkey.destinations.persistence.repositories.PlayerRepository;
import com.github.mmonkey.destinations.utilities.PlayerUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.Optional;

public class PlayerListeners {

    @Listener
    public void onClientConnectionEventLogin(ClientConnectionEvent.Login event) {
        Optional<Player> optionalPlayer = event.getTargetUser().getPlayer();
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            Optional<PlayerEntity> optionalEntity = PlayerRepository.instance.get(player.getIdentifier());
            if (optionalEntity.isPresent()) {
                PlayerEntity playerEntity = optionalEntity.get();
                if (!playerEntity.getName().equals(player.getName())) {
                    playerEntity.setName(player.getName());
                    PlayerRepository.instance.save(playerEntity);
                }
            } else {
                PlayerRepository.instance.save(new PlayerEntity(player));
            }
        }
    }

    @Listener
    public void onPlayerBackLocationSaveEvent(PlayerBackLocationSaveEvent event) {
        if (!event.isCancelled()) {
            PlayerEntity playerEntity = PlayerUtil.getPlayerEntity(event.getPlayer());

            String worldIdentifier = event.getPlayer().getWorld().getUniqueId().toString();
            playerEntity.getBacks().forEach(back -> {
                if (back.getLocation().getWorld().getIdentifier().equals(worldIdentifier)) {
                    playerEntity.getBacks().remove(back);
                }
            });

            playerEntity.getBacks().add(new BackEntity(new LocationEntity(event.getPlayer())));
            PlayerRepository.instance.save(playerEntity);
        }
    }

    @Listener
    public void onDestructEntityEvent(DestructEntityEvent event) {
        if (DestinationsConfig.isBackCommandEnabled() && DestinationsConfig.allowBackOnDeath() && event.getTargetEntity() instanceof Player) {
            Sponge.getGame().getEventManager().post(new PlayerBackLocationSaveEvent((Player) event.getTargetEntity()));
        }
    }

}
