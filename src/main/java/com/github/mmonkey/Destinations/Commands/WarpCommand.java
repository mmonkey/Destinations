package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Dams.BackDam;
import com.github.mmonkey.Destinations.Dams.WarpDam;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Events.PlayerBackLocationSaveEvent;
import com.github.mmonkey.Destinations.Models.WarpModel;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;

public class WarpCommand implements CommandExecutor {

	private Destinations plugin;
	private WarpDam warpDam;
	private BackDam backDam;
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";
		Player player = (Player) src;

		ArrayList<WarpModel> warps = this.getWarps(player);
		WarpModel warp = this.searchWarps(warps, name);

		if (warp == null) {
			
			player.sendMessage(
				Text.of(FormatUtil.ERROR, "Warp ", FormatUtil.OBJECT, name, FormatUtil.ERROR, " does not exist.")
			);
			
			return CommandResult.success();
			
		}
		
		if (!warp.isPublic() && !warp.getWhitelist().containsKey(player.getUniqueId()) && !warp.getOwnerUniqueId().equals(player.getUniqueId())) {
			
			player.sendMessage(
				Text.of(FormatUtil.ERROR, "You do not have access to warp: ", FormatUtil.OBJECT, name, FormatUtil.ERROR, ".")
			);
			
			return CommandResult.success();
			
		}

		plugin.getGame().getEventManager().post(new PlayerBackLocationSaveEvent(player));
		player.setRotation(warp.getDestination().getRotation());
		player.setLocation(warp.getDestination().getLocation(plugin.getGame()));
		
		return CommandResult.success();
		
	}

	private ArrayList<WarpModel> getWarps(Player player) {
		// this is broken in sponge
		// return (player.hasPermission("warp.admin")) ? warpDam.getAllWarps() : warpDam.getPlayerWarps(player);
		return warpDam.getPlayerWarps(player);
	}

	public WarpModel searchWarps(ArrayList<WarpModel> warps, String name) {

		for (WarpModel warp : warps) {
			if (warp.getName().equalsIgnoreCase(name)) {
				return warp;
			}
		}

		return null;
	}
	
	public WarpCommand(Destinations plugin) {
		this.plugin = plugin;
		this.warpDam = new WarpDam(plugin);
	}
}
