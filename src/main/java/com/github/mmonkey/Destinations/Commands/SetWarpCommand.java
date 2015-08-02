package com.github.mmonkey.Destinations.Commands;

import java.util.ArrayList;
import java.util.UUID;

import com.github.mmonkey.Destinations.Dams.WarpDam;
import com.github.mmonkey.Destinations.Models.WarpModel;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.github.mmonkey.Destinations.Models.DestinationModel;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;

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
				Texts.of(FormatUtil.ERROR, "Warp ", FormatUtil.OBJECT, name, FormatUtil.ERROR, " already exists and cannot be added.").builder().build()
			);

			return CommandResult.success();
			
		}

		WarpModel warp = warpDam.insertWarp(player, name, true);
		
		player.sendMessage(
			Texts.of(FormatUtil.SUCCESS, "Warp ", FormatUtil.OBJECT, warp.getName(), FormatUtil.SUCCESS, " was successfully created!").builder().build()
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
