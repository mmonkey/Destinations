package com.github.mmonkey.Destinations.Commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.github.mmonkey.Destinations.Home;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;

public class SetHomeCommand implements CommandExecutor {
	
	private Destinations plugin;

	public SetHomeCommand(Destinations plugin) {
		this.plugin = plugin;
	}
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		boolean force = (args.hasAny("f"))  ? (Boolean) args.getOne("f").get() : false;
		String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";
		
		Player player = (Player) src;
		List<String> list = plugin.getHomeStorageService().getHomeList(player);
		Home home = createHome(player, name);
		
		if (force && list.contains(home.getName())) {
			
			plugin.getHomeStorageService().updateHome(player, home);
			
			player.sendMessage(
				Texts.of(FormatUtil.SUCCESS, "Home ", FormatUtil.OBJECT, home.getName(), FormatUtil.SUCCESS, " has been updated to this location!").builder()
				.build()
			);
			
			return CommandResult.success();
			
		}
		
		if (list.contains(home.getName())) {
			
			player.sendMessage(
				Texts.of(FormatUtil.ERROR, "Home ", FormatUtil.OBJECT, home.getName(), FormatUtil.ERROR, " already exists!").builder()
				.build()
			);
			
			return CommandResult.success();
			
		}
		
		plugin.getHomeStorageService().addHome(player, home);
		
		player.sendMessage(
			Texts.of(FormatUtil.SUCCESS, "Home ", FormatUtil.OBJECT, home.getName(), FormatUtil.SUCCESS, " was successfully created!").builder()
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
		
		return home;

	}
	
	/**
	 * Returns available home name, example: home4
	 * @param homes ArrayList<Home>
	 * @return String
	 */
	private String getAvailableName(ArrayList<Home> homes) {
		
		int max = 0;
		int temp = 0;
		
		for (Home home: homes) {
			
			if (home.getName().startsWith("home") && home.getName().matches(".*\\d.*")) {
				temp = Integer.parseInt(home.getName().replaceAll("[\\D]", ""));
			}
			
			if(temp > max) {
				max = temp;
			}
			
		}
		
		if (homes.size() > max) {
			max = homes.size();
		}
		
		return (homes.size() == 0) ? "home" : "home" + Integer.toString(max + 1);
		
	}

}
