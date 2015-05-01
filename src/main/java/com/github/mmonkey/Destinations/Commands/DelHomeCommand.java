package com.github.mmonkey.Destinations.Commands;

import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandMessageFormatting;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import com.github.mmonkey.Destinations.Utilities.HomeUtil;

public class DelHomeCommand implements CommandExecutor {
	
	private Destinations plugin;

	public DelHomeCommand(Destinations plugin) {
		this.plugin = plugin;
	}
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		boolean force = (args.hasAny("f")) ? (Boolean) args.getOne("f").get() : false;
		boolean cancel = (args.hasAny("c")) ? (Boolean) args.getOne("c").get() : false;
		String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";
		Player player = (Player) src;
		List<String> list = plugin.getHomeStorageService().getHomeList(player);
		
		if (cancel) {
			
			player.sendMessage(
				Texts.of(FormatUtil.SUCCESS, "Home ", FormatUtil.OBJECT, name, FormatUtil.SUCCESS, " was not deleted.").builder()
				.build()
			);
			
			return CommandResult.success();
			
		}
		
		if (force && list.contains(name)) {
			
			deleteHome(player, name);
			return CommandResult.success();
				
		}
		
		if (list.contains(name)) {
			
			HomeUtil homeUtil = new HomeUtil();
			
			player.sendMessage(
				Texts.of(CommandMessageFormatting.NEWLINE_TEXT).builder()
				.append(Texts.of(FormatUtil.DIALOG, "Are you sure you want to delete home ", FormatUtil.OBJECT, name, FormatUtil.DIALOG, "?  "))
				.append(homeUtil.getDeleteHomeConfirmationLink(name, "Yes"))
				.append(Texts.of("  "))
				.append(homeUtil.getDeleteHomeCancelLink(name, "No"))
				.append(CommandMessageFormatting.NEWLINE_TEXT)
				.build()
			);
			
			return CommandResult.success();
		
		} else {
			
			player.sendMessage(
				Texts.of(FormatUtil.ERROR, "Home ", FormatUtil.DELETED_OBJECT, name, FormatUtil.ERROR, " doesn't exist.").builder()
				.build()
			);
			
			return CommandResult.success();
			
		}

	}
	
	private void deleteHome(Player player, String name) {
		
		if (plugin.getHomeStorageService().removeHome(player, name)) {
			
			player.sendMessage(
				Texts.of(FormatUtil.SUCCESS, "Home ", FormatUtil.DELETED_OBJECT, name, FormatUtil.SUCCESS, " was successfully deleted!").builder()
				.build()
			);
			
		} else {
			
			player.sendMessage(
				Texts.of(FormatUtil.ERROR, "Home ", FormatUtil.DELETED_OBJECT, name, FormatUtil.ERROR, " doesn't exist, and could not be deleted.").builder()
				.build()
			);
			
		}

	}

}
