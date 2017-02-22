package com.github.mmonkey.destinations.listeners;

import com.github.mmonkey.destinations.Destinations;
import com.github.mmonkey.destinations.configs.DestinationsConfig;
import com.github.mmonkey.destinations.entities.BackEntity;
import com.github.mmonkey.destinations.entities.LocationEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.events.interfaces.PlayerTeleportEvent;
import com.github.mmonkey.destinations.exceptions.EconomyServiceNotFoundException;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.persistence.repositories.PlayerRepository;
import com.github.mmonkey.destinations.utilities.EconomyUtil;
import com.github.mmonkey.destinations.utilities.MessagesUtil;
import org.spongepowered.api.data.manipulator.mutable.entity.SleepingData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.filter.type.Include;
import org.spongepowered.api.util.Tristate;

import java.math.BigDecimal;
import java.util.Optional;

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
        Optional<SleepingData> optional = event.getTargetEntity().get(SleepingData.class);
        if (optional.isPresent() && optional.get().asImmutable().sleeping().get()) {
            event.getTargetEntity().sendMessage(MessagesUtil.get(event.getTargetEntity(), "teleport.sleep"));
            return;
        }

        if (DestinationsConfig.isEconomyEnabled() && event.getCost().compareTo(BigDecimal.ZERO) > 0) {
            try {
                if (!EconomyUtil.instance.chargePlayer(event.getTargetEntity(), event.getCost())) {
                    event.getTargetEntity().sendMessage(MessagesUtil.error(event.getTargetEntity(), "economy.not_enough_funds"));
                    return;
                }
            } catch (EconomyServiceNotFoundException e) {
                Destinations.getInstance().getLogger().error(e.getMessage());
            }
        }

        if (event.getRotation() == null) {
            event.getTargetEntity().setLocationSafely(event.getLocation());
        } else {
            event.getTargetEntity().setLocationAndRotationSafely(event.getLocation(), event.getRotation());
        }
    }

}
