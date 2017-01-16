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

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {

        if (!(source instanceof Player)) {
            return CommandResult.empty();
        }

        BackEntity back = null;
        Player player = (Player) source;
        PlayerEntity playerEntity = PlayerUtil.getPlayerEntity(player);
        for (BackEntity backEntity : playerEntity.getBacks()) {
            if (backEntity.getLocation().getWorld().getIdentifier().equals(player.getWorld().getUniqueId().toString())) {
                back = backEntity;
            }
        }

        Sponge.getGame().getEventManager().post(new PlayerBackLocationSaveEvent(player));

        if (back == null) {
            return CommandResult.empty();
        }

        player.setLocationAndRotationSafely(back.getLocation().getLocation(), back.getLocation().getRotation());
        return CommandResult.success();
    }

}
