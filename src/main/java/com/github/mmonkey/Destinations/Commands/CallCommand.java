package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CallCommand implements CommandExecutor {

	private Destinations plugin;

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}

		Player caller = (Player) src;
		Player target = args.getOne("player").isPresent() ? (Player) args.getOne("player").get() : null;

		if (target == null) {
            caller.sendMessage(Text.of(FormatUtil.ERROR, "Invalid player."));
			return CommandResult.empty();
		}

        if (plugin.getCallService().isCalling(caller, target)) {
            Text.Builder message = Text.builder();
            message.append(Text.of(FormatUtil.ERROR, "You must wait until your current call to "));
            message.append(Text.of(FormatUtil.OBJECT, target.getName()));
            message.append(Text.of(FormatUtil.ERROR, " expires before calling them again."));

            caller.sendMessage(message.build());
            return CommandResult.success();
        }

        Text.Builder message = Text.builder();
        message.append(Text.of(FormatUtil.OBJECT, caller.getName(), FormatUtil.DIALOG, " has requested a teleport type "));
        message.append(BringCommand.getBringAction(caller.getName()));
        message.append(Text.of(FormatUtil.DIALOG, " to teleport them to you."));

        // Send messages
		target.sendMessage(message.build());
        caller.sendMessage(Text.of(FormatUtil.OBJECT, target.getName(), FormatUtil.DIALOG, " was called."));

        plugin.getCallService().call(caller, target);

		return CommandResult.success();
	}

	public CallCommand(Destinations plugin) {
		this.plugin = plugin;
	}
}
