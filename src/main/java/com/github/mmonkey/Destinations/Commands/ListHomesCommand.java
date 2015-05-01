package com.github.mmonkey.Destinations.Commands;

import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.TextBuilder;
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
import com.github.mmonkey.Destinations.Utilities.PaginationUtil;

public class ListHomesCommand implements CommandExecutor {
	
	private static final int ITEMS_PER_PAGE = 7;
	
	private Destinations plugin;

	public ListHomesCommand(Destinations plugin) {
		this.plugin = plugin;
	}
	
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		String page = (args.hasAny("page")) ? ((String) args.getOne("page").get()) : "";
		int pageNum = 1;
		
		try {
			pageNum = (Integer.parseInt(page) == 0) ? 1 : Integer.parseInt(page);
		} catch (NumberFormatException e) {}
			
		Player player = (Player) src;
		List<String> list = plugin.getHomeStorageService().getHomeList(player);
		
		if (list.isEmpty()) {
			player.sendMessage(Texts.of(FormatUtil.ERROR, "No home has been set!"));
			return CommandResult.success();
		}
		
		int numPages = (int) Math.ceil(list.size() / (double)ITEMS_PER_PAGE);
		
		if (pageNum > numPages) {
			pageNum = numPages;
		}
		
		TextBuilder listText = Texts.builder();
		FormatUtil format = new FormatUtil();
		HomeUtil homeUtil = new HomeUtil();
		
		listText.append(format.empty());
		listText.append(Texts.of(FormatUtil.HEADLINE, format.getFill(10, '-') + " Showing homes page " + pageNum + " of " + numPages + " " + format.getFill(10, '-')));
		listText.append(CommandMessageFormatting.NEWLINE_TEXT);
		
		int startIndex = (pageNum - 1) * ITEMS_PER_PAGE;
		int index = startIndex + 1;
		for (int i = startIndex; i < startIndex + ITEMS_PER_PAGE; i++) {
			
			if (list.size() > i) {
				
				listText.append(Texts.of(FormatUtil.DIALOG, (index < 10) ?  "  " + index + ") " : index + ") "));
				listText.append(homeUtil.getHomeLink(list.get(i)), Texts.of(" - "));
				listText.append(homeUtil.getDeleteHomeLink(list.get(i), "delete"));
				listText.append(CommandMessageFormatting.NEWLINE_TEXT);
			
			}
			
			index++;
		
		}
		
		PaginationUtil pagination = new PaginationUtil(pageNum, numPages, "/listhomes");
		
		listText.append(CommandMessageFormatting.NEWLINE_TEXT);
		listText.append(Texts.of(format.getFill(10, '-')), pagination.getPrevPagination());
		listText.append(Texts.of(FormatUtil.OBJECT, " " + pageNum + " "));
		listText.append(pagination.getNextPagination(), Texts.of(format.getFill(10, '-')));
		
		player.sendMessage(listText.build());
		
		return CommandResult.success();

	}

}
