package com.github.mmonkey.Destinations.Commands;

import java.util.UUID;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.github.mmonkey.Destinations.Destination;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Warp;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;

public class SetWarpCommand implements CommandExecutor {

	private Destinations plugin;
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";
		Player player = (Player) src;
		Warp existing = plugin.getWarpStorageService().getWarp(name);
		
		if (existing != null) {
			
			player.sendMessage(
				Texts.of(FormatUtil.ERROR, "Warp ", FormatUtil.OBJECT, name, FormatUtil.ERROR, " already exists and cannot be added.").builder().build()
			);
			
		}
		
		Destination destination = new Destination(player);
		UUID uniqueId = player.getUniqueId();
		
		Warp warp = new Warp();
		warp.setName(name);
		warp.setOwnerUniqueId(uniqueId);
		warp.getWhitelist().put(uniqueId, true);
		warp.setDestination(destination);
		
		plugin.getWarpStorageService().addWarp(warp);
		
		player.sendMessage(
			Texts.of(FormatUtil.SUCCESS, "Warp ", FormatUtil.OBJECT, name, FormatUtil.SUCCESS, " was successfully created!").builder().build()
		);
		
		return CommandResult.success();
		
	}
	
	public SetWarpCommand(Destinations plugin) {
		this.plugin = plugin;
	}
	
}
