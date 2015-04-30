package com.github.mmonkey.Destinations.Commands;

import java.util.ArrayList;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.github.mmonkey.Destinations.Home;
import com.github.mmonkey.Destinations.Destinations;

public class SetHomeCommand implements CommandExecutor {
	
	private Destinations plugin;

	public SetHomeCommand(Destinations plugin) {
		this.plugin = plugin;
	}
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";
		Player player = (Player) src;
		Home home = createHome(player, name);
		
		player.sendMessage(
			Texts.of(TextColors.GREEN, "Home ").builder()
			.append(Texts.of(TextColors.GOLD, home.getName()))
			.append(Texts.of(TextColors.GREEN, " was successfully created!"))
			.build()
		);
		
		return CommandResult.success();

	}
	
	/**
	 * Creates a new home of given name, if no name is passed, one will be generated
	 * 
	 * @param player Player
	 * @param name String
	 * @return Home
	 */
	private Home createHome(Player player, String name) {

		ArrayList<Home> playerHomes = plugin.getHomeStorageService().getHomes(player);
		Home home = (name.equals("")) ? new Home(getAvailableName(playerHomes), player) : new Home(name, player);
		
		plugin.getHomeStorageService().addHome(player, home);
		
		return home;

	}
	
	/**
	 * Returns available home name, example: home4
	 * @param homes ArrayList<Home>
	 * @return String
	 */
	private String getAvailableName(ArrayList<Home> homes) {
		return (homes.size() == 0) ? "home" : "home" + Integer.toString(homes.size() + 1);
	}

}
