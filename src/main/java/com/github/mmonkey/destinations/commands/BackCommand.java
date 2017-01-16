package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.BackEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.events.PlayerBackLocationSaveEvent;
import com.github.mmonkey.destinations.utilities.PlayerUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class BackCommand implements CommandExecutor {

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.success();
        }

        Player player = (Player) src;
        PlayerEntity playerEntity = PlayerUtil.getPlayerEntity(player);
        Sponge.getGame().getEventManager().post(new PlayerBackLocationSaveEvent(player));

        playerEntity.getBacks().forEach((BackEntity back) -> {
            if (back.getLocation().getWorld().getIdentifier().equals(player.getWorld().getUniqueId().toString())) {
                player.setRotation(back.getLocation().getRotation());
                player.setLocation(back.getLocation().getLocation());
            }
        });

        return CommandResult.success();
    }

}
