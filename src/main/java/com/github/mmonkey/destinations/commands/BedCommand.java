package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.BedEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.events.PlayerBackLocationSaveEvent;
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

        PlayerEntity playerEntity = PlayerUtil.getPlayerEntityWithBeds(player);
        BedEntity bed = PlayerUtil.getBed(playerEntity, player);
        if (bed != null) {
            Sponge.getEventManager().post(new PlayerBackLocationSaveEvent(player));
            player.setLocationSafely(bed.getLocation().getLocation());
            return CommandResult.success();
        }

        return CommandResult.empty();
    }

}
