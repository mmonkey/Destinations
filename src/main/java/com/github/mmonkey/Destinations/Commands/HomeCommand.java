package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Dams.HomeDam;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Events.PlayerBackLocationSaveEvent;
import com.github.mmonkey.Destinations.Models.HomeModel;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.util.ArrayList;

public class HomeCommand implements CommandExecutor {
	
	private Destinations plugin;
	private HomeDam homeDam;

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.success();
		}
		
		String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";
		Player player = (Player) src;
		ArrayList<HomeModel> homes = homeDam.getPlayerHomes(player);

		if (homes.isEmpty()) {	
			player.sendMessage(Text.of(FormatUtil.ERROR, "No home has been set!"));
			return CommandResult.success();
		}
			
		HomeModel home = (name.equals("")) ? getClosestHome(player, homes) : getNamedHome(homes, name);

		if (home == null) {
			player.sendMessage(Text.of(FormatUtil.ERROR, "You have no home named ", FormatUtil.OBJECT, name, FormatUtil.ERROR, "."));
			return CommandResult.success();
		}

		plugin.getGame().getEventManager().post(new PlayerBackLocationSaveEvent(player));
        player.setRotation(home.getDestination().getRotation());
        player.setLocation(home.getDestination().getLocation(plugin.getGame()));
			
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
		this.homeDam = new HomeDam(plugin);
	}

}
