package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.SpawnEntity;
import com.github.mmonkey.destinations.persistence.cache.SpawnCache;
import com.github.mmonkey.destinations.persistence.repositories.SpawnRepository;
import com.github.mmonkey.destinations.utilities.MessagesUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class SetSpawnCommand implements CommandExecutor {

    public static final String[] ALIASES = {"setspawn"};

    /**
     * Get the Command Specifications for this command
     *
     * @return CommandSpec
     */
    public static CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("destinations.spawn.create")
                .description(Text.of("/setspawn"))
                .extendedDescription(Text.of("Set this worlds spawn location."))
                .executor(new SetSpawnCommand())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        Player player = (Player) src;
        for (SpawnEntity spawn : SpawnCache.instance.get()) {
            if (spawn.getWorld().getIdentifier().equals(player.getWorld().getUniqueId().toString())) {
                SpawnCache.instance.get().remove(spawn);
                SpawnRepository.instance.remove(spawn);
            }
        }

        SpawnEntity spawnEntity = SpawnRepository.instance.save(new SpawnEntity(player));
        SpawnCache.instance.get().add(spawnEntity);
        player.getWorld().getProperties().setSpawnPosition(player.getLocation().getBlockPosition());

        player.sendMessage(MessagesUtil.success(player, "spawn.create"));
        return CommandResult.success();
    }

}
