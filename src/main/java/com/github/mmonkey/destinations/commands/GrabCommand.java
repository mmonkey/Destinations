package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.events.PlayerTeleportGrabEvent;
import com.github.mmonkey.destinations.events.PlayerTeleportPreEvent;
import com.github.mmonkey.destinations.utilities.MessagesUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class GrabCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        Player requester = (Player) src;
        Player target = (Player) args.getOne("player").orElse(null);

        if (target == null) {
            requester.sendMessage(MessagesUtil.error(requester, "grab.invalid_player"));
            return CommandResult.success();
        }

        Sponge.getGame().getEventManager().post(new PlayerTeleportPreEvent(target, target.getLocation(), target.getRotation()));
        Sponge.getGame().getEventManager().post(new PlayerTeleportGrabEvent(target, requester.getLocation(), requester.getRotation()));

        target.sendMessage(MessagesUtil.success(target, "grab.teleport", requester.getName()));
        return CommandResult.success();
    }

}
