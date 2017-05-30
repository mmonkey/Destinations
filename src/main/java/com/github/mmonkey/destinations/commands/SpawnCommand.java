package com.github.mmonkey.destinations.commands;

import com.flowpowered.math.vector.Vector3d;
import com.github.mmonkey.destinations.entities.SpawnEntity;
import com.github.mmonkey.destinations.events.PlayerTeleportPreEvent;
import com.github.mmonkey.destinations.events.PlayerTeleportSpawnEvent;
import com.github.mmonkey.destinations.persistence.cache.SpawnCache;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class SpawnCommand implements CommandExecutor {

    public static final String[] ALIASES = {"spawn", "s"};

    /**
     * Get the Command Specifications for this command
     *
     * @return CommandSpec
     */
    public static CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("destinations.spawn.use")
                .description(Text.of("/spawn"))
                .extendedDescription(Text.of("Teleports the player to their current world's spawn location."))
                .executor(new SpawnCommand())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        Player player = (Player) src;
        Location<World> location = new Location<>(player.getWorld(), player.getWorld().getProperties().getSpawnPosition());
        Vector3d rotation = null;
        for (SpawnEntity spawnEntity : SpawnCache.instance.get()) {
            if (spawnEntity.getWorld().getIdentifier().equals(player.getWorld().getUniqueId().toString())) {
                rotation = spawnEntity.getRotation();
            }
        }

        Sponge.getGame().getEventManager().post(new PlayerTeleportPreEvent(player, player.getLocation(), player.getRotation()));
        Sponge.getGame().getEventManager().post(new PlayerTeleportSpawnEvent(player, location, rotation));
        return CommandResult.empty();
    }

}
