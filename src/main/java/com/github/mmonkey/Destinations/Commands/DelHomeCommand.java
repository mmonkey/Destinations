package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Dams.HomeDam;
import com.github.mmonkey.Destinations.Models.HomeModel;
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
import com.github.mmonkey.Destinations.Utilities.FormatUtil;

public class DelHomeCommand implements CommandExecutor {
	
	private Destinations plugin;
	private HomeDam homeDam;
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		boolean force = (args.hasAny("f")) ? (Boolean) args.getOne("f").get() : false;
		boolean cancel = (args.hasAny("c")) ? (Boolean) args.getOne("c").get() : false;
		String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";

		Player player = (Player) src;
		HomeModel home = homeDam.getPlayerHomeByName(player, name);
		
		if (cancel) {
			
			player.sendMessage(
				Texts.of(FormatUtil.SUCCESS, "Home ", FormatUtil.OBJECT, name, FormatUtil.SUCCESS, " was not deleted.").builder()
				.build()
			);
			
			return CommandResult.success();
			
		}
		
		if (force && home != null) {
			
			deleteHome(player, home);
			return CommandResult.success();
				
		}
		
		if (home != null) {
			
			player.sendMessage(
				Texts.of(CommandMessageFormatting.NEWLINE_TEXT).builder()
				.append(Texts.of(FormatUtil.DIALOG, "Are you sure you want to delete home ", FormatUtil.OBJECT, name, FormatUtil.DIALOG, "?  "))
				.append(getDeleteHomeConfirmationAction(name, "Yes"))
				.append(Texts.of("  "))
				.append(getDeleteHomeCancelAction(name, "No"))
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
	
	private void deleteHome(Player player, HomeModel home) {
		
		if (homeDam.deleteHome(home)) {

            player.sendMessage(
				Texts.of(FormatUtil.empty(), FormatUtil.SUCCESS, "Home ", FormatUtil.DELETED_OBJECT, home.getName(), FormatUtil.SUCCESS, " was successfully deleted!").builder()
				.build()
			);
			
		} else {
			
			player.sendMessage(
				Texts.of(FormatUtil.ERROR, "Home ", FormatUtil.DELETED_OBJECT, home.getName(), FormatUtil.ERROR, " doesn't exist, and could not be deleted.").builder()
				.build()
			);
			
		}

	}
	
	private Text getDeleteHomeConfirmationAction(String name, String linkText) {
		
		return Texts.builder(linkText)
			.onClick(TextActions.runCommand("/delhome -f " + name))
			.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Delete home ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.CONFIRM)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	private Text getDeleteHomeCancelAction(String name, String linkText) {
		
		return Texts.builder(linkText)
			.onClick(TextActions.runCommand("/delhome -c " + name))
			.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Do not delete home ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.CANCEL)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	public DelHomeCommand(Destinations plugin) {
		this.plugin = plugin;
		this.homeDam = new HomeDam(plugin);
	}

}
