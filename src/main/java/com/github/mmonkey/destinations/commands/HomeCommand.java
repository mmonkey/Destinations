package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.HomeEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.events.PlayerTeleportHomeEvent;
import com.github.mmonkey.destinations.events.PlayerTeleportPreEvent;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.utilities.FormatUtil;
import com.github.mmonkey.destinations.utilities.PlayerUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.util.Set;

public class HomeCommand implements CommandExecutor {

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        String name = (String) args.getOne("name").orElse("");

        Player player = (Player) src;
        PlayerEntity playerEntity = PlayerCache.instance.get(player);
        Set<HomeEntity> homes = playerEntity.getHomes();

        if (homes.isEmpty()) {
            player.sendMessage(Text.of(FormatUtil.ERROR, "No home has been set!"));
            return CommandResult.success();
        }

        HomeEntity home = name.equals("") ? getClosestHome(playerEntity, player.getLocation()) : PlayerUtil.getPlayerHomeByName(playerEntity, name);

        if (home == null) {
            player.sendMessage(Text.of(FormatUtil.ERROR, "You have no home named ", FormatUtil.OBJECT, name, FormatUtil.ERROR, "."));
            return CommandResult.success();
        }

        Sponge.getGame().getEventManager().post(new PlayerTeleportPreEvent(player, player.getLocation(), player.getRotation()));
        Sponge.getGame().getEventManager().post(new PlayerTeleportHomeEvent(player, home.getLocation().getLocation(), home.getLocation().getRotation()));
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
