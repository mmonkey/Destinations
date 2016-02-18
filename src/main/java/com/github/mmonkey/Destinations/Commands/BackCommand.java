package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Dams.BackDam;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Events.PlayerBackLocationSaveEvent;
import com.github.mmonkey.Destinations.Models.BackModel;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class BackCommand implements CommandExecutor {

    private Destinations plugin;
    private BackDam backDam;

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.success();
        }

        Player player = (Player) src;

        BackModel back = backDam.getBack(player);
        plugin.getGame().getEventManager().post(new PlayerBackLocationSaveEvent(player));

        if (back != null) {
            player.setRotation(back.getDestination().getRotation());
            player.setLocation(back.getDestination().getLocation(plugin.getGame()));
        }

        return CommandResult.success();

    }

    public BackCommand(Destinations plugin) {
        this.plugin = plugin;
        this.backDam = new BackDam(plugin);
    }

}
