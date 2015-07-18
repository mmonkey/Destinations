package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Services.CallService;
import com.google.common.base.Optional;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

public class CallCommand implements CommandExecutor {

	private final CallService callService;

	public CallCommand(CallService callService) {
		this.callService = callService;
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}

		Player srcAsPlayer = (Player) src;
		String srcName = srcAsPlayer.getName();

		Optional<Player> playerOptional = args.getOne("call-ee");

		if (!playerOptional.isPresent()) {
			return CommandResult.empty();
		}

		Player player = playerOptional.get();

		callService.call(player, srcAsPlayer);
		src.sendMessage(Texts.of(player.getName() + " was called."));

		Text callText = Texts
				.builder(
						srcAsPlayer.getName()
								+ " has requested a teleport type ")
				.append(BringCommand.getBringAction(srcName))
				.append(Texts.of(" to teleport them to you.")).build();

		player.sendMessage(callText);

		return CommandResult.success();
	}
}
