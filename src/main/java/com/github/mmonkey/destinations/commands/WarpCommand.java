package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.commands.elements.WarpCommandElement;
import com.github.mmonkey.destinations.configs.DestinationsConfig;
import com.github.mmonkey.destinations.entities.AccessEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.entities.WarpEntity;
import com.github.mmonkey.destinations.events.PlayerTeleportPreEvent;
import com.github.mmonkey.destinations.events.PlayerTeleportWarpEvent;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.persistence.cache.WarpCache;
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

public class WarpCommand implements CommandExecutor {

    public static final String[] ALIASES = {"warp", "w"};

    /**
     * Get the Command Specifications for this command
     *
     * @return CommandSpec
     */
    public static CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("destinations.warp.use")
                .description(Text.of("/warp <name>"))
                .extendedDescription(Text.of("Teleport to the warp of the provided name."))
                .executor(new WarpCommand())
                .arguments(new WarpCommandElement(Text.of("name")))
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        String name = (String) args.getOne("name").orElse("");
        Player player = (Player) src;
        PlayerEntity playerEntity = PlayerCache.instance.get(player);
        WarpEntity warp = this.searchWarps(name);

        if (warp == null) {
            player.sendMessage(MessagesUtil.error(player, "warp.does_not_exist", name));
            return CommandResult.success();
        }

        if (warp.isPrivate() && !warp.getOwner().getIdentifier().equals(playerEntity.getIdentifier())) {
            boolean hasAccess = false;
            for (AccessEntity access : warp.getAccess()) {
                if (access.getPlayer().getIdentifier().equals(playerEntity.getIdentifier())) {
                    hasAccess = true;
                }
            }

            if (!hasAccess) {
                player.sendMessage(MessagesUtil.error(player, "warp.no_access", name));
                return CommandResult.success();
            }
        }

        BigDecimal cost = BigDecimal.valueOf(
                DestinationsConfig.getInstance().get().getNode(DestinationsConfig.ECONOMY_SETTINGS, "costWarpCommand").getDouble(0)
        );
        Sponge.getGame().getEventManager().post(new PlayerTeleportPreEvent(player, player.getLocation(), player.getRotation()));
        Sponge.getGame().getEventManager().post(new PlayerTeleportWarpEvent(player, warp.getLocation().getLocation(), warp.getLocation().getRotation(), cost));
        return CommandResult.success();
    }

    private WarpEntity searchWarps(String name) {
        for (WarpEntity warp : WarpCache.instance.get()) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }
        return null;
    }

}
