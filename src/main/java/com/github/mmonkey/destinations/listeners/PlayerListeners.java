package com.github.mmonkey.destinations.listeners;

import com.github.mmonkey.destinations.configs.DestinationsConfig;
import com.github.mmonkey.destinations.entities.BackEntity;
import com.github.mmonkey.destinations.entities.BedEntity;
import com.github.mmonkey.destinations.entities.LocationEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.events.PlayerBackLocationSaveEvent;
import com.github.mmonkey.destinations.persistence.repositories.PlayerRepository;
import com.github.mmonkey.destinations.utilities.PlayerUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.SleepingEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

public class PlayerListeners {

    @Listener
    @IsCancelled(Tristate.FALSE)
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
    @IsCancelled(Tristate.FALSE)
    public void onPlayerBackLocationSaveEvent(PlayerBackLocationSaveEvent event) {
        PlayerEntity playerEntity = PlayerUtil.getPlayerEntityWithBacks(event.getPlayer());

        String worldIdentifier = event.getPlayer().getWorld().getUniqueId().toString();
        playerEntity.getBacks().forEach(back -> {
            if (back.getLocation().getWorld().getIdentifier().equals(worldIdentifier)) {
                playerEntity.getBacks().remove(back);
            }
        });

        playerEntity.getBacks().add(new BackEntity(new LocationEntity(event.getPlayer())));
        PlayerRepository.instance.save(playerEntity);
    }

    @Listener
    public void onDestructEntityEvent(DestructEntityEvent event, @Root Player player) {
        if (DestinationsConfig.isBackCommandEnabled() && DestinationsConfig.allowBackOnDeath()) {
            Sponge.getGame().getEventManager().post(new PlayerBackLocationSaveEvent(player));
        }
    }

    @Listener
    @IsCancelled(Tristate.FALSE)
    public void onSleepingEvent(SleepingEvent.Pre event) {
        if (!(event.getTargetEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getTargetEntity();
        PlayerEntity playerEntity = PlayerUtil.getPlayerEntityWithBeds(player);
        BlockSnapshot blockSnapshot = event.getBed();
        Optional<Location<World>> optional = blockSnapshot.getLocation();
        if (!optional.isPresent()) {
            return;
        }

        Location<World> location = optional.get();
        for (BedEntity bed : playerEntity.getBeds()) {
            if (bed.getLocation().getWorld().getIdentifier().equals(player.getWorld().getUniqueId().toString())) {
                Location<World> bedLocation = bed.getLocation().getLocation();
                if (location.getBlockPosition().equals(bedLocation.getBlockPosition())) {
                    bed.setLastUse(new Timestamp(new Date().getTime()));
                    PlayerRepository.instance.save(playerEntity);
                    return;
                }
            }
        }

        playerEntity.getBeds().add(new BedEntity(new LocationEntity(location)));
        PlayerRepository.instance.save(playerEntity);
    }

}
