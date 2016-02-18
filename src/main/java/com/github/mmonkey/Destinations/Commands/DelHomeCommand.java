package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Dams.HomeDam;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Models.HomeModel;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;

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
				Text.of(FormatUtil.SUCCESS, "Home ", FormatUtil.OBJECT, name, FormatUtil.SUCCESS, " was not deleted.")
			);
			
			return CommandResult.success();
			
		}
		
		if (force && home != null) {
			
			deleteHome(player, home);
			return CommandResult.success();
				
		}
		
		if (home != null) {
			
			player.sendMessage(
				Text.builder(System.lineSeparator())
				.append(Text.of(FormatUtil.DIALOG, "Are you sure you want to delete home ", FormatUtil.OBJECT, name, FormatUtil.DIALOG, "?  "))
				.append(getDeleteHomeConfirmationAction(name, "Yes"))
				.append(Text.of("  "))
				.append(getDeleteHomeCancelAction(name, "No"))
				.append(Text.of(System.lineSeparator()))
				.build()
			);
			
			return CommandResult.success();
		
		} else {
			
			player.sendMessage(
				Text.of(FormatUtil.ERROR, "Home ", FormatUtil.DELETED_OBJECT, name, FormatUtil.ERROR, " doesn't exist.")
			);
			
			return CommandResult.success();
			
		}

	}
	
	private void deleteHome(Player player, HomeModel home) {
		
		if (homeDam.deleteHome(home)) {

            player.sendMessage(
				Text.of(FormatUtil.empty(), FormatUtil.SUCCESS, "Home ", FormatUtil.DELETED_OBJECT, home.getName(), FormatUtil.SUCCESS, " was successfully deleted!")
			);
			
		} else {
			
			player.sendMessage(
				Text.of(FormatUtil.ERROR, "Home ", FormatUtil.DELETED_OBJECT, home.getName(), FormatUtil.ERROR, " doesn't exist, and could not be deleted.")
			);
			
		}

	}
	
	private Text getDeleteHomeConfirmationAction(String name, String linkText) {
		
		return Text.builder(linkText)
			.onClick(TextActions.runCommand("/delhome -f " + name))
			.onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Delete home ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.CONFIRM)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	private Text getDeleteHomeCancelAction(String name, String linkText) {
		
		return Text.builder(linkText)
			.onClick(TextActions.runCommand("/delhome -c " + name))
			.onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Do not delete home ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.CANCEL)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	public DelHomeCommand(Destinations plugin) {
		this.plugin = plugin;
		this.homeDam = new HomeDam(plugin);
	}

}
