package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.commands.elements.HomeCommandElement;
import com.github.mmonkey.destinations.configs.DestinationsConfig;
import com.github.mmonkey.destinations.entities.HomeEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.events.PlayerTeleportHomeEvent;
import com.github.mmonkey.destinations.events.PlayerTeleportPreEvent;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.utilities.MessagesUtil;
import com.github.mmonkey.destinations.utilities.PlayerUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.math.BigDecimal;
import java.util.Set;

public class HomeCommand implements CommandExecutor {

    public static final String[] ALIASES = {"home", "h"};

    /**
     * Get the Command Specifications for this command
     *
     * @return CommandSpec
     */
    public static CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("destinations.home.use")
                .description(Text.of("/home [name]"))
                .extendedDescription(Text.of("Teleport to the nearest home or to the named home."))
                .executor(new HomeCommand())
                .arguments(GenericArguments.optional(new HomeCommandElement(Text.of("name"))))
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
        Set<HomeEntity> homes = playerEntity.getHomes();

        if (homes.isEmpty()) {
            player.sendMessage(MessagesUtil.error(player, "home.empty"));
            return CommandResult.success();
        }

        HomeEntity home = name.equals("") ? getClosestHome(playerEntity, player.getLocation()) : PlayerUtil.getPlayerHomeByName(playerEntity, name);
        if (home == null) {
            player.sendMessage(MessagesUtil.error(player, "home.does_not_exist", name));
            return CommandResult.success();
        }

        BigDecimal cost = BigDecimal.valueOf(
                DestinationsConfig.getInstance().get().getNode(DestinationsConfig.ECONOMY_SETTINGS, "costHomeCommand").getDouble(0)
        );
        Sponge.getGame().getEventManager().post(new PlayerTeleportPreEvent(player, player.getLocation(), player.getRotation()));
        Sponge.getGame().getEventManager().post(new PlayerTeleportHomeEvent(player, home.getLocation().getLocation(), home.getLocation().getRotation(), cost));
        return CommandResult.success();
    }

    /**
     * Calculate the closest home to the player's current location
     *
     * @param player Player
     * @return HomeEntity|null
     */
    private HomeEntity getClosestHome(PlayerEntity player, Location playerLocation) {

        // TODO: should we take worlds into account here?

        double min = -1;
        double tmp;
        HomeEntity result = null;

        for (HomeEntity home : player.getHomes()) {

            Location location = home.getLocation().getLocation();
            double x = Math.pow((playerLocation.getX() - location.getX()), 2);
            double y = Math.pow((playerLocation.getY() - location.getY()), 2);
            double z = Math.pow((playerLocation.getZ() - location.getZ()), 2);
            tmp = Math.sqrt(x + y + z);

            if (min == -1 || tmp < min) {
                min = tmp;
                result = home;
            }
        }

        return result;
    }

}
