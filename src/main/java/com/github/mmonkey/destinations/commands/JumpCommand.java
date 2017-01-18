package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.events.PlayerBackLocationSaveEvent;
import com.github.mmonkey.destinations.utilities.BlockUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class JumpCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        Player player = (Player) src;
        BlockRay<World> blockRay = BlockRay.from(player).skipFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1)).build();
        Location<World> location = null;

        while (blockRay.hasNext() && location == null) {
            BlockRayHit<World> hit = blockRay.next();
            if (BlockUtil.isSolid(hit.getLocation())) {
                location = hit.getLocation();
            }
        }

        if (location != null) {
            Sponge.getEventManager().post(new PlayerBackLocationSaveEvent(player));
            player.setLocationSafely(location);
            return CommandResult.success();
        }

        return CommandResult.empty();
    }

}
