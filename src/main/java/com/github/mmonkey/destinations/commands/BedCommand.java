package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.BedEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.events.PlayerTeleportBedEvent;
import com.github.mmonkey.destinations.events.PlayerTeleportPreEvent;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.utilities.PlayerUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.manipulator.mutable.entity.SleepingData;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public class BedCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        Player player = (Player) src;
        Optional<SleepingData> optional = player.get(SleepingData.class);
        if (optional.isPresent() && optional.get().asImmutable().sleeping().get()) {
            return CommandResult.empty();
        }

        PlayerEntity playerEntity = PlayerCache.instance.get(player);
        BedEntity bed = PlayerUtil.getBed(playerEntity, player);
        if (bed != null) {
            Sponge.getGame().getEventManager().post(new PlayerTeleportPreEvent(player, player.getLocation(), player.getRotation()));
            Sponge.getGame().getEventManager().post(new PlayerTeleportBedEvent(player, bed.getLocation().getLocation(), player.getRotation()));
            return CommandResult.success();
        }

        return CommandResult.empty();
    }

}
