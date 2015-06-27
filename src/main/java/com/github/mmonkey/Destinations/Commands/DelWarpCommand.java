package com.github.mmonkey.Destinations.Commands;

import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandMessageFormatting;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Warp;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;

public class DelWarpCommand implements CommandExecutor {

	private Destinations plugin;
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		boolean force = (args.hasAny("f")) ? (Boolean) args.getOne("f").get() : false;
		boolean cancel = (args.hasAny("c")) ? (Boolean) args.getOne("c").get() : false;
		String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";
		Player player = (Player) src;
		List<String> list = plugin.getWarpStorageService().getPlayerWarpList(player);
		
		if (cancel) {
			
			player.sendMessage(
				Texts.of(FormatUtil.SUCCESS, "Warp ", FormatUtil.OBJECT, name, FormatUtil.SUCCESS, " was not deleted.").builder()
				.build()
			);
			
			return CommandResult.success();
			
		}
		
		if (force && list.contains(name)) {
			
			deleteWarp(player, name);
			return CommandResult.success();
				
		}
		
		if (list.contains(name)) {
			
			player.sendMessage(
				Texts.of(CommandMessageFormatting.NEWLINE_TEXT).builder()
				.append(Texts.of(FormatUtil.DIALOG, "Are you sure you want to delete warp ", FormatUtil.OBJECT, name, FormatUtil.DIALOG, "?  "))
				.append(getDeleteWarpConfirmationAction(name, "Yes"))
				.append(Texts.of("  "))
				.append(getDeleteWarpCancelAction(name, "No"))
				.append(CommandMessageFormatting.NEWLINE_TEXT)
				.build()
			);
			
			return CommandResult.success();
		
		} else {
			
			player.sendMessage(
				Texts.of(FormatUtil.ERROR, "Warp ", FormatUtil.DELETED_OBJECT, name, FormatUtil.ERROR, " doesn't exist, or you don't have permissions to delete it.").builder()
				.build()
			);
			
			return CommandResult.success();
			
		}
		
	}
	
	private void deleteWarp(Player player, String name) {
		
		Warp warp = plugin.getWarpStorageService().getWarp(name);
		
		if (warp != null && plugin.getWarpStorageService().removeWarp(warp)) {
			
			player.sendMessage(
				Texts.of(FormatUtil.SUCCESS, "Warp ", FormatUtil.DELETED_OBJECT, name, FormatUtil.SUCCESS, " was successfully deleted!").builder()
				.build()
			);
			
		} else {
			
			player.sendMessage(
				Texts.of(FormatUtil.ERROR, "Warp ", FormatUtil.DELETED_OBJECT, name, FormatUtil.ERROR, " doesn't exist, or you don't have permissions to delete it.").builder()
				.build()
			);
			
		}

	}
	
	private Text getDeleteWarpConfirmationAction(String name, String linkText) {
		
		return Texts.builder(linkText)
			.onClick(TextActions.runCommand("/delwarp -f " + name))
			.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Delete warp ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.CONFIRM)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	private Text getDeleteWarpCancelAction(String name, String linkText) {
		
		return Texts.builder(linkText)
			.onClick(TextActions.runCommand("/delwarp -c " + name))
			.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Do not delete warp ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.CANCEL)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	public DelWarpCommand(Destinations plugin) {
		this.plugin = plugin;
	}
}
