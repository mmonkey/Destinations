package com.github.mmonkey.Destinations.Commands;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Warp;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;

public class WarpCommand implements CommandExecutor {

	private Destinations plugin;
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";
		Player player = (Player) src;
		Warp warp = plugin.getWarpStorageService().getWarp(name);
		
		if (warp == null) {
			
			player.sendMessage(
				Texts.of(FormatUtil.ERROR, "Warp ", FormatUtil.OBJECT, name, FormatUtil.ERROR, " does not exist.").builder().build()
			);
			
			return CommandResult.success();
			
		}
		
		if (!warp.isPublic() && warp.getWhitelist().containsKey(player.getUniqueId()) && !warp.getOwnerUniqueId().equals(player.getUniqueId())) {
			
			player.sendMessage(
				Texts.of(FormatUtil.ERROR, "You do not have access to warp: ", FormatUtil.OBJECT, name, FormatUtil.ERROR, ".").builder().build()
			);
			
			return CommandResult.success();
			
		}
		
		player.setRotation(warp.getDestination().getRotation());
		player.setLocation(warp.getDestination().getLocation(plugin.getGame()));
		
		return CommandResult.success();
		
	}
	
	public WarpCommand(Destinations plugin) {
		this.plugin = plugin;
	}
}
