package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Events.PlayerBackLocationSaveEvent;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class GrabCommand implements CommandExecutor {

    private Destinations plugin;

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        Player requester = (Player) src;
        Player target = args.getOne("player").isPresent() ? (Player) args.getOne("player").get() : null;

        if (target == null) {

            requester.sendMessage(Text.of(FormatUtil.ERROR, "Invalid player."));
            return CommandResult.success();

        }

        plugin.getGame().getEventManager().post(new PlayerBackLocationSaveEvent(target));
        target.setRotation(requester.getRotation());
        target.setLocation(requester.getLocation());

        Text.Builder message = Text.builder();
        message.append(Text.of(FormatUtil.DIALOG, "You have been teleported to "));
        message.append(Text.of(FormatUtil.OBJECT, requester.getName(), FormatUtil.DIALOG, "."));
        target.sendMessage(message.build());

        return CommandResult.success();
    }

    public GrabCommand(Destinations plugin) {
        this.plugin = plugin;
    }
}
