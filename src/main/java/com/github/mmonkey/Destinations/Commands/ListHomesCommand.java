package com.github.mmonkey.Destinations.Commands;

import java.util.Iterator;
import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.world.World;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Pagination.PaginatedList;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;

public class ListHomesCommand implements CommandExecutor {
	
	private Destinations plugin;
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		// Only allow players to use this command
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		Player player = (Player) src;
		
		// Get the page number from arguments, defaults to 1
		int currentPage = (args.hasAny("page")) ? (Integer) args.getOne("page").get() : 1;
		
		// Get this players list of homes
		List<String> list = plugin.getHomeStorageService().getHomeList(player);
		
		// If this player doesn't have any homes, return with message
		if (list.isEmpty()) {
			player.sendMessage(Texts.of(FormatUtil.ERROR, "No home has been set!").builder().build());
			return CommandResult.success();
		}
		
		// Get utility classes and new PaginatedList
		TextBuilder message = Texts.builder();
		TextBuilder header = Texts.builder();
		PaginatedList paginatedList = new PaginatedList("/listhomes");
		
		// Fill paginatedList with items
		for (String name: list) {
			
			TextBuilder row = Texts.builder();
			row.append(getHomeAction(name), Texts.of(" - "));
			row.append(getDeleteHomeAction(name, "delete"));
			
			paginatedList.add(row.build());
		}
		
		// Created header for paginatedList
		header.append(Texts.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
		header.append(Texts.of(FormatUtil.HEADLINE, " Showing homes page " + currentPage + " of " + paginatedList.getTotalPages() + " "));
		header.append(Texts.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
		
		// Add header to paginatedList
		paginatedList.setHeader(header.build());
		
		// Clear the chat window
		message.append(FormatUtil.empty());
		
		// Add the paginated list to the message
		message.append(paginatedList.getPage(currentPage));
		
		// Send message to this player
		player.sendMessage(message.build());
		
		Iterator<World> worlds = plugin.getGame().getServer().getWorlds().iterator();
		while (worlds.hasNext()) {
			World w = worlds.next();
			player.sendMessage(Texts.of(w.getName() + ": " + w.getUniqueId().toString()));
		}
		
		return CommandResult.success();

	}
	
	private Text getHomeAction(String name) {
		
		return Texts.builder(name)
			.onClick(TextActions.runCommand("/home " + name))
			.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Teleport to ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.GENERIC_LINK)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	private Text getDeleteHomeAction(String name, String linkText) {
		
		return Texts.builder(linkText)
			.onClick(TextActions.runCommand("/delhome " + name))
			.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "Delete home ", FormatUtil.OBJECT, name)))
			.color(FormatUtil.DELETE)
			.style(TextStyles.UNDERLINE)
			.build();
	
	}
	
	public ListHomesCommand(Destinations plugin) {
		this.plugin = plugin;
	}

}
