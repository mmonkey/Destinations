package com.github.mmonkey.destinations.listeners;

import com.flowpowered.math.vector.Vector3d;
import com.github.mmonkey.destinations.configs.DestinationsConfig;
import com.github.mmonkey.destinations.entities.BedEntity;
import com.github.mmonkey.destinations.entities.LocationEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.entities.SpawnEntity;
import com.github.mmonkey.destinations.events.PlayerTeleportPreEvent;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.persistence.cache.SpawnCache;
import com.github.mmonkey.destinations.persistence.repositories.PlayerRepository;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.SleepingEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
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
            PlayerEntity playerEntity = PlayerCache.instance.get(player);
            if (!playerEntity.getName().equals(player.getName())) {
                playerEntity.setName(player.getName());
                playerEntity = PlayerRepository.instance.save(playerEntity);
                PlayerCache.instance.set(player, playerEntity);
            }
        }
    }

    @Listener
    public void onDestructEntityEvent(DestructEntityEvent event, @Root Player player) {
        if (DestinationsConfig.isBackCommandEnabled() && DestinationsConfig.allowBackOnDeath()) {
            Sponge.getGame().getEventManager().post(new PlayerTeleportPreEvent(player, player.getLocation(), player.getRotation()));
        }
    }

    @Listener
    public void onRespawnPlayerEvent(RespawnPlayerEvent event) {
        if (event.isBedSpawn()) {
            return;
        }

        World world = event.getToTransform().getExtent();
        Vector3d rotation = null;
        for (SpawnEntity spawnEntity : SpawnCache.instance.get()) {
            if (spawnEntity.getWorld().getIdentifier().equals(world.getUniqueId().toString())) {
                rotation = spawnEntity.getRotation();
            }
        }

        if (rotation != null) {
            Transform<World> to = new Transform<>(world.getSpawnLocation());
            event.setToTransform(to.setRotation(rotation));
        }
    }

    @Listener
    @IsCancelled(Tristate.FALSE)
    public void onMoveEntityEventTeleport(MoveEntityEvent.Teleport event, @Root Player player) {
        if (event.getFromTransform().getExtent().equals(event.getToTransform().getExtent())) {
            return;
        }

        World world = event.getToTransform().getExtent();
        Vector3d rotation = null;
        for (SpawnEntity spawnEntity : SpawnCache.instance.get()) {
            if (spawnEntity.getWorld().getIdentifier().equals(world.getUniqueId().toString())) {
                rotation = spawnEntity.getRotation();
            }
        }

        if (rotation != null) {
            Transform<World> to = new Transform<>(world.getSpawnLocation());
            event.setToTransform(to.setRotation(rotation));
        }
    }

    @Listener
    @IsCancelled(Tristate.FALSE)
    public void onSleepingEvent(SleepingEvent.Pre event) {
        if (!(event.getTargetEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getTargetEntity();
        PlayerEntity playerEntity = PlayerCache.instance.get(player);
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
                    playerEntity = PlayerRepository.instance.save(playerEntity);
                    PlayerCache.instance.set(player, playerEntity);
                    return;
                }
            }
        }

        playerEntity.getBeds().add(new BedEntity(new LocationEntity(location)));
        playerEntity = PlayerRepository.instance.save(playerEntity);
        PlayerCache.instance.set(player, playerEntity);
    }

}
