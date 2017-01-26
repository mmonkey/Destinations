package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.BedEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.events.PlayerTeleportBedEvent;
import com.github.mmonkey.destinations.events.PlayerTeleportPreEvent;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.utilities.MessagesUtil;
import com.github.mmonkey.destinations.utilities.PlayerUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class BedCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        Player player = (Player) src;
        PlayerEntity playerEntity = PlayerCache.instance.get(player);
        BedEntity bed = PlayerUtil.getBed(playerEntity, player);
        if (bed != null) {
            Sponge.getGame().getEventManager().post(new PlayerTeleportPreEvent(player, player.getLocation(), player.getRotation()));
            Sponge.getGame().getEventManager().post(new PlayerTeleportBedEvent(player, bed.getLocation().getLocation(), player.getRotation()));
            return CommandResult.success();
        }

        player.sendMessage(MessagesUtil.warning(player, "bed.empty"));
        return CommandResult.empty();
    }

}
