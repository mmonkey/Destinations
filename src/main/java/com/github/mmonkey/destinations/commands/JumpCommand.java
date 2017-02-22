package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.configs.DestinationsConfig;
import com.github.mmonkey.destinations.events.PlayerTeleportJumpEvent;
import com.github.mmonkey.destinations.events.PlayerTeleportPreEvent;
import com.github.mmonkey.destinations.utilities.BlockUtil;
import com.github.mmonkey.destinations.utilities.MessagesUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;

public class JumpCommand implements CommandExecutor {

    public static final String[] ALIASES = {"jump", "j"};

    /**
     * Get the Command Specifications for this command
     *
     * @return CommandSpec
     */
    public static CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("destinations.jump")
                .description(Text.of("/jump"))
                .extendedDescription(Text.of("Teleports the player where they are looking."))
                .executor(new JumpCommand())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        Player player = (Player) src;
        BlockRay<World> blockRay = BlockRay.from(player).skipFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 5)).build();
        Location<World> location = null;

        while (blockRay.hasNext() && location == null) {
            BlockRayHit<World> hit = blockRay.next();
            if (BlockUtil.isSolid(hit.getLocation())) {
                location = hit.getLocation();
            }
        }

        if (location != null) {
            BigDecimal cost = BigDecimal.valueOf(
                    DestinationsConfig.getInstance().get().getNode(DestinationsConfig.ECONOMY_SETTINGS, "costJumpCommand").getDouble(0)
            );
            Sponge.getGame().getEventManager().post(new PlayerTeleportPreEvent(player, player.getLocation(), player.getRotation()));
            Sponge.getGame().getEventManager().post(new PlayerTeleportJumpEvent(player, location, player.getRotation(), cost));
            return CommandResult.success();
        }

        player.sendMessage(MessagesUtil.error(player, "jump.error"));
        return CommandResult.empty();
    }

}
