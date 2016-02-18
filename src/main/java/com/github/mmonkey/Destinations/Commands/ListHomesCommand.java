package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Dams.HomeDam;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Models.HomeModel;
import com.github.mmonkey.Destinations.Pagination.PaginatedList;
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

import java.util.ArrayList;

public class ListHomesCommand implements CommandExecutor {
	
	private Destinations plugin;
	private HomeDam homeDam;
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		// Only allow players to use this command
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		Player player = (Player) src;
		
		// Get the page number from arguments, defaults to 1
		int currentPage = (args.hasAny("page")) ? (Integer) args.getOne("page").get() : 1;
		
		// Get this players list of homes
		ArrayList<HomeModel> homes = homeDam.getPlayerHomes(player);
		ArrayList<String> list = this.getHomeNames(homes);
		
		// If this player doesn't have any homes, return with message
		if (list.isEmpty()) {
			player.sendMessage(Text.of(FormatUtil.ERROR, "No home has been set!"));
			return CommandResult.success();
		}
		
		// Get utility classes and new PaginatedList
		Text.Builder message = Text.builder();
		Text.Builder header = Text.builder();
		PaginatedList paginatedList = new PaginatedList("/listhomes");
		
		// Fill paginatedList with items
		for (String name: list) {
			
			Text.Builder row = Text.builder();
			row.append(getHomeAction(name), Text.of(" - "));
			row.append(getDeleteHomeAction(name, "delete"));
			
			paginatedList.add(row.build());
		}

		currentPage = currentPage > paginatedList.getTotalPages() ? paginatedList.getTotalPages() : currentPage;
		
		// Created header for paginatedList
		header.append(Text.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
		header.append(Text.of(FormatUtil.HEADLINE, " Showing homes page " + currentPage + " of " + paginatedList.getTotalPages() + " "));
		header.append(Text.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
		
		// Add header to paginatedList
		paginatedList.setHeader(header.build());
		
		// Clear the chat window
		message.append(FormatUtil.empty());
		
		// Add the paginated list to the message
		message.append(paginatedList.getPage(currentPage));
		
		// Send message to this player
		player.sendMessage(message.build());
		
		return CommandResult.success();

	}

	private ArrayList<String> getHomeNames(ArrayList<HomeModel> homes) {

		ArrayList<String> list = new ArrayList<String>();
		for (HomeModel home: homes) {
			list.add(home.getName());
		}

		return list;

	}
	
	private Text getHomeAction(String name) {
		
		return Text.builder(name)
			.onClick(TextActions.runCommand("/home " + name))
			.onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Teleport to ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.GENERIC_LINK)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	private Text getDeleteHomeAction(String name, String linkText) {
		
		return Text.builder(linkText)
			.onClick(TextActions.runCommand("/delhome " + name))
			.onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Delete home ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.DELETE)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	public ListHomesCommand(Destinations plugin) {
		this.plugin = plugin;
		this.homeDam = new HomeDam(plugin);
	}

}
