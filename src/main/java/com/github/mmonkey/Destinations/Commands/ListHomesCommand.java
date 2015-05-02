package com.github.mmonkey.Destinations.Commands;

import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import com.github.mmonkey.Destinations.Utilities.HomeUtil;
import com.github.mmonkey.Destinations.Utilities.PaginatedList;

public class ListHomesCommand implements CommandExecutor {
	
	private Destinations plugin;

	public ListHomesCommand(Destinations plugin) {
		this.plugin = plugin;
	}
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		String page = (args.hasAny("page")) ? ((String) args.getOne("page").get()) : "";
		int currentPage = 1;
		
		try {
			currentPage = (Integer.parseInt(page) == 0) ? 1 : Integer.parseInt(page);
		} catch (NumberFormatException e) {}
			
		Player player = (Player) src;
		List<String> list = plugin.getHomeStorageService().getHomeList(player);
		
		if (list.isEmpty()) {
			player.sendMessage(Texts.of(FormatUtil.ERROR, "No home has been set!"));
			return CommandResult.success();
		}
		
		FormatUtil format = new FormatUtil();
		HomeUtil homeUtil = new HomeUtil();
		
		TextBuilder message = Texts.builder();
		PaginatedList paginatedList = new PaginatedList("/listhomes");
		
		for (String name: list) {
			TextBuilder link = Texts.builder();
			link.append(homeUtil.getHomeLink(name), Texts.of(" - "));
			link.append(homeUtil.getDeleteHomeLink(name, "delete"));
			
			paginatedList.add(link.build());
		}
		
		paginatedList.setHeader(Texts.of(FormatUtil.HEADLINE, format.getFill(10, '-') + " Showing homes page " + currentPage + " of " + paginatedList.getTotalPages() + " " + format.getFill(10, '-')));
		
		message.append(format.empty());
		message.append(paginatedList.getPage(currentPage));
		player.sendMessage(message.build());
		
		return CommandResult.success();

	}

}
