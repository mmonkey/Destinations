package com.github.mmonkey.Destinations.Commands;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.github.mmonkey.Destinations.Destinations;

public class DelHomeCommand implements CommandExecutor {
	
	private Destinations plugin;

	public DelHomeCommand(Destinations plugin) {
		this.plugin = plugin;
	}
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		String name =  (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";
		Player player = (Player) src;
		
		if (plugin.getHomeStorageService().removeHome(player, name)) {
			
			player.sendMessage(
				Texts.of(TextColors.YELLOW, "Home ", TextColors.RED, name, TextColors.YELLOW, " was successfully deleted!").builder()
				.build()
			);
			
			return CommandResult.success();
			
		} else {
			
			player.sendMessage(
				Texts.of(TextColors.RED, "Home ", TextColors.YELLOW, name, TextColors.RED, " doesn't exist, and could not be deleted.").builder()
				.build()
			);
			
			return CommandResult.success();
			
		}

	}

}
