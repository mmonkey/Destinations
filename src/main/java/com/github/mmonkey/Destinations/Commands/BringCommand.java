package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Pagination.PaginatedList;
import com.github.mmonkey.Destinations.Services.CallService;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import com.google.common.base.Optional;
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

import java.util.List;

public class BringCommand implements CommandExecutor {

	private CallService callService;

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}
		
		Optional<Player> playerOptional = args.getOne("caller");
		Player srcAsPlayer = (Player) src;
		
		if (playerOptional.isPresent()) {
		
			return executeBring(playerOptional.get(), srcAsPlayer, args);
		
		} else {
		
			return listCallers(srcAsPlayer, args);
		
		}
	}

	private CommandResult listCallers(Player src, CommandContext args) {
		
		List<String> callList = callService.getCalling(src);
		PaginatedList pager = new PaginatedList("/bring");
		int currentPage = (args.hasAny("page")) ? (Integer) args.getOne("page").get() : 1;

		for (String name : callList) {
			TextBuilder row = Texts.builder();
			row.append(getBringAction(name));
			pager.add(row.build());
		}

		TextBuilder header = Texts.builder();
		TextBuilder message = Texts.builder();

		header.append(Texts.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
		header.append(Texts.of(FormatUtil.HEADLINE, " Showing homes page " + currentPage + " of " + pager.getTotalPages() + " "));
		header.append(Texts.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
		pager.setHeader(header.build());
		
		// clear the chat
		message.append(FormatUtil.empty());
		message.append(pager.getPage(currentPage));
		src.sendMessage(message.build());

		return CommandResult.success();

	}

	private CommandResult executeBring(Player player, Player src, CommandContext args) {
		
		player.setLocation(src.getLocation());
		player.sendMessage(Texts.of("You have been teleported to " + src.getName()));
		return CommandResult.success();
	
	}

	public static Text getBringAction(String name) {
		
		return Texts.builder("/bring " + name)
				.onClick(TextActions.runCommand("/bring " + name))
				.onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "/bring ", FormatUtil.OBJECT, name)))
				.color(FormatUtil.GENERIC_LINK).style(TextStyles.UNDERLINE)
				.build();
	
	}

	public BringCommand(CallService callService) {
		this.callService = callService;
	}

}
