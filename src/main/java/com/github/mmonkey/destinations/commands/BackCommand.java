package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.configs.DestinationsConfig;
import com.github.mmonkey.destinations.entities.BackEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.events.PlayerTeleportBackEvent;
import com.github.mmonkey.destinations.events.PlayerTeleportPreEvent;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
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

import java.math.BigDecimal;

public class BackCommand implements CommandExecutor {

    public static final String[] ALIASES = {"back", "b"};

    /**
     * Get the Command Specifications for this command
     *
     * @return CommandSpec
     */
    public static CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("destinations.back")
                .description(Text.of("/back"))
                .extendedDescription(Text.of("Returns you to your last position from a prior teleport."))
                .executor(new BackCommand())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext arguments) throws CommandException {

        if (!(source instanceof Player)) {
            return CommandResult.empty();
        }

        BackEntity back = null;
        Player player = (Player) source;
        PlayerEntity playerEntity = PlayerCache.instance.get(player);
        for (BackEntity backEntity : playerEntity.getBacks()) {
            if (backEntity.getLocation().getWorld().getIdentifier().equals(player.getWorld().getUniqueId().toString())) {
                back = backEntity;
            }
        }

        Sponge.getGame().getEventManager().post(new PlayerTeleportPreEvent(player, player.getLocation(), player.getRotation()));
        if (back != null) {
            Sponge.getGame().getEventManager().post(
                    new PlayerTeleportBackEvent(player, back.getLocation().getLocation(), back.getLocation().getRotation())
            );
            return CommandResult.success();
        }

        player.sendMessage(MessagesUtil.error(player, "back.error"));
        return CommandResult.empty();
    }

}
