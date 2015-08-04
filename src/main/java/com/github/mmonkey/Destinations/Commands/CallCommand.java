package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import com.google.common.base.Optional;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

public class CallCommand implements CommandExecutor {

	private Destinations plugin;

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}

		Player caller = (Player) src;
		String callerName = caller.getName();

		Player callee = args.getOne("player").isPresent() ? (Player) args.getOne("player").get() : null;

		if (callee == null) {
            caller.sendMessage(Texts.of(FormatUtil.ERROR, "Invalid player."));
			return CommandResult.empty();
		}

        TextBuilder message = Texts.builder();
        message.append(Texts.of(FormatUtil.OBJECT, caller.getName(), FormatUtil.DIALOG, " has requested a teleport type "));
        message.append(BringCommand.getBringAction(callerName));
        message.append(Texts.of(FormatUtil.DIALOG, " to teleport them to you."));

        // Send messages
		callee.sendMessage(message.build());
        caller.sendMessage(Texts.of(FormatUtil.OBJECT, callee.getName(), FormatUtil.DIALOG, " was called."));

        plugin.getCallService().call(callee, caller);

		return CommandResult.success();
	}

	public CallCommand(Destinations plugin) {
		this.plugin = plugin;
	}
}
