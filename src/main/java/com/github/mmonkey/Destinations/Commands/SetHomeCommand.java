package com.github.mmonkey.Destinations.Commands;

import java.util.ArrayList;

import com.github.mmonkey.Destinations.Dams.HomeDam;
import com.github.mmonkey.Destinations.Models.HomeModel;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;

public class SetHomeCommand implements CommandExecutor {

	private HomeDam homeDam;
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		boolean force = (args.hasAny("f"))  ? (Boolean) args.getOne("f").get() : false;
		String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";
		
		Player player = (Player) src;
		ArrayList<HomeModel> homes = homeDam.getPlayerHomes(player);
		HomeModel home = homeDam.getPlayerHomeByName(player, name);
		
//		if (force && list.contains(home.getName())) {
//
//			plugin.getHomeStorageService().updateHome(player, home);
//
//			player.sendMessage(
//				Texts.of(FormatUtil.SUCCESS, "HomeModel ", FormatUtil.OBJECT, home.getName(), FormatUtil.SUCCESS, " has been updated to this location!").builder()
//				.build()
//			);
//
//			return CommandResult.success();
//
//		}
		
		if (home != null) {
			
			player.sendMessage(
				Texts.of(FormatUtil.ERROR, "HomeModel ", FormatUtil.OBJECT, name, FormatUtil.ERROR, " already exists!").builder()
				.build()
			);
			
			return CommandResult.success();
			
		}

		name = name.equals("") ? getAvailableName(homes) : name;
		homeDam.saveHome(player, name);
		
		player.sendMessage(
			Texts.of(FormatUtil.SUCCESS, "HomeModel ", FormatUtil.OBJECT, name, FormatUtil.SUCCESS, " was successfully created!").builder()
			.build()
		);
		
		return CommandResult.success();

	}
	
	/**
	 * Returns available home name, example: home4
	 *
	 * @param homes ArrayList<HomeModel>
	 * @return String
	 */
	private String getAvailableName(ArrayList<HomeModel> homes) {
		
		int max = 0;
		int temp = 0;
		
		for (HomeModel home: homes) {
			
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
	
	public SetHomeCommand(Destinations plugin) {
		this.homeDam = new HomeDam(plugin.getDatabase());
	}

}
