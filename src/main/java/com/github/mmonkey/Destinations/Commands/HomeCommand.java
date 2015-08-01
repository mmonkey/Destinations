package com.github.mmonkey.Destinations.Commands;

import java.util.ArrayList;
import java.util.Collection;

import com.github.mmonkey.Destinations.Dams.HomeDam;
import com.github.mmonkey.Destinations.Models.HomeModel;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.world.Location;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import org.spongepowered.api.world.World;

public class HomeCommand implements CommandExecutor {
	
	private Destinations plugin;
	private HomeDam homeDam;

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.success();
		}
		
		String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";
		Player player = (Player) src;
		ArrayList<HomeModel> homes = this.filterHomes(homeDam.getPlayerHomes(player));
		
		if (homes.isEmpty()) {	
			player.sendMessage(Texts.of(FormatUtil.ERROR, "No home has been set!").builder().build());
			return CommandResult.success();
		}
			
		HomeModel home = (name.equals("")) ? getClosestHome(player, homes) : getNamedHome(homes, name);
		Location location = (home != null) ? home.getDestination().getLocation(plugin.getGame()) : null;
			
		if (location != null) {
			player.setRotation(home.getDestination().getRotation());
			player.setLocation(location);
		}
		
		// TODO add no home found if location == null
			
		return CommandResult.success();
	}
	
	/**
	 * Calculate the closest home to the player's current location
	 * 
	 * @param player Player
	 * @param homes ArrayList<HomeModel>
	 * @return HomeModel|null
	 */
	private HomeModel getClosestHome(Player player, ArrayList<HomeModel> homes) {
		
		Location playerLocation = player.getLocation();
		
		double min = -1;
		double tmp;
		HomeModel result = null;
		
		for (HomeModel home: homes) {
			
			Location location = home.getDestination().getLocation(plugin.getGame());
			double x = Math.pow((playerLocation.getX() - location.getX()), 2);
			double y = Math.pow((playerLocation.getY() - location.getY()), 2);
			double z = Math.pow((playerLocation.getZ() - location.getZ()), 2);
			tmp = Math.sqrt(x + y + z);
			
			if (min == -1 || tmp < min) {
				min = tmp;
				result = home;
			}
		}
		
		return result;
		
	}

	private ArrayList<HomeModel> filterHomes(ArrayList<HomeModel> homes) {

		Collection<World> worlds = plugin.getGame().getServer().getWorlds();
		ArrayList<HomeModel> list = new ArrayList<HomeModel>();
		for (World world: worlds) {
			for (HomeModel home: homes) {
				if (home.getDestination().getWorldUniqueId().equals(world.getUniqueId())) {
					list.add(home);
				}
			}
		}

		return list;

	}
	
	/**
	 * Get the HomeModel of the given name.
	 *
	 * @param homes ArrayList<HomeModel>
	 * @param name String
	 * @return HomeModel|null
	 */
	public HomeModel getNamedHome(ArrayList<HomeModel> homes, String name) {

		for (HomeModel home: homes) {
			if (home.getName().equals(name)) {
				return home;
			}
		}
		
		return null;
		
	}
	
	public HomeCommand(Destinations plugin) {
		this.plugin = plugin;
		this.homeDam = new HomeDam(plugin.getDatabase());
	}

}
