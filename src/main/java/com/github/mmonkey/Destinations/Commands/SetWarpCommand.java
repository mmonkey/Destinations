package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Dams.WarpDam;
import com.github.mmonkey.Destinations.Destinations;
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

public class SetWarpCommand implements CommandExecutor {

	private Destinations plugin;
	private WarpDam warpDam;
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";
		Player player = (Player) src;
		
		if (warpExists(name)) {
			
			player.sendMessage(
				Text.of(FormatUtil.ERROR, "Warp ", FormatUtil.OBJECT, name, FormatUtil.ERROR, " already exists and cannot be added.")
			);

			return CommandResult.success();
			
		}

		WarpModel warp = warpDam.insertWarp(player, name, true);
		
		player.sendMessage(
			Text.of(FormatUtil.SUCCESS, "Warp ", FormatUtil.OBJECT, warp.getName(), FormatUtil.SUCCESS, " was successfully created!")
		);

		// TODO: add private flag
		
		return CommandResult.success();
		
	}

	public boolean warpExists(String name) {

		ArrayList<WarpModel> allWarps = this.warpDam.getAllWarps();
		for (WarpModel warp : allWarps) {
			if (warp.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}

		return false;
	}
	
	public SetWarpCommand(Destinations plugin) {
		this.plugin = plugin;
		this.warpDam = new WarpDam(plugin);
	}
	
}
