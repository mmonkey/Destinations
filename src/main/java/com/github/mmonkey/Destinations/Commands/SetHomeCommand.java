package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Dams.HomeDam;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Models.HomeModel;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;

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
        ArrayList<String> list = this.getHomeNames(homes);
		
		if (force && list.contains(home.getName())) {

            home = homeDam.updateHome(player, home);

			player.sendMessage(
				Text.of(FormatUtil.SUCCESS, "Home ", FormatUtil.OBJECT, home.getName(), FormatUtil.SUCCESS, " has been updated to this location")
			);

			return CommandResult.success();

		}
		
		if (home != null) {
			
			player.sendMessage(
				Text.of(FormatUtil.ERROR, "Home ", FormatUtil.OBJECT, name, FormatUtil.ERROR, " already exists!")
			);
			
			return CommandResult.success();
			
		}

		name = name.equals("") ? getAvailableName(homes) : name;
		home = homeDam.insertHome(player, name);
		
		player.sendMessage(
			Text.of(FormatUtil.SUCCESS, "Home ", FormatUtil.OBJECT, home.getName(), FormatUtil.SUCCESS, " was successfully created!")
		);
		
		return CommandResult.success();

	}

	private ArrayList<String> getHomeNames(ArrayList<HomeModel> homes) {

		ArrayList<String> list = new ArrayList<String>();
		for (HomeModel home: homes) {
			list.add(home.getName());
		}

		return list;

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
		this.homeDam = new HomeDam(plugin);
	}

}
