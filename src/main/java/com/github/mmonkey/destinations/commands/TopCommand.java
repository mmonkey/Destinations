package com.github.mmonkey.destinations.commands;

import com.flowpowered.math.vector.Vector3d;
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

public class TopCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        Player player = (Player) src;
        Location<World> location = null;
        Vector3d start = new Vector3d(player.getLocation().getPosition().getX(), 256, player.getLocation().getPosition().getZ());
        Vector3d end = new Vector3d(player.getLocation().getPosition().getX(), 0, player.getLocation().getPosition().getZ());
        BlockRay<World> blockRay = BlockRay.from(player.getWorld(), start).to(end)
                .skipFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1)).build();

        while (blockRay.hasNext() && location == null) {
            BlockRayHit<World> hit = blockRay.next();
            if (BlockUtil.isSolid(hit.getLocation())) {
                location = hit.getLocation();
            }
        }

        if (location != null && (location.getBlockY() != player.getLocation().getBlockY() - 1)) {
            Sponge.getEventManager().post(new PlayerBackLocationSaveEvent(player));
            player.setLocationSafely(location);
            return CommandResult.success();
        }

        return CommandResult.empty();
    }

}
