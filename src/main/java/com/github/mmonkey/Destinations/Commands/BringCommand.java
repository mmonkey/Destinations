package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Events.PlayerBackLocationSaveEvent;
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

	private Destinations plugin;

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}

        Player callee = (Player) src;
		Player caller = args.getOne("caller").isPresent() ? (Player) args.getOne("caller").get() : null;
		
		if (caller == null) {

            return listCallers(callee, args);

        }

        if (plugin.getCallService().isCalling(caller, callee)) {

            return executeBring(caller, callee, args);

        } else {

            TextBuilder message = Texts.builder();

            message.append(Texts.of(FormatUtil.WARN, "Call request from "));
            message.append(Texts.of(FormatUtil.OBJECT, caller.getName()));
            message.append(Texts.of(FormatUtil.WARN, " has expired."));

            callee.sendMessage(message.build());
            return CommandResult.success();
        }

	}

	private CommandResult listCallers(Player callee, CommandContext args) {
		
		List<String> callList = plugin.getCallService().getCalling(callee);
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
		header.append(Texts.of(FormatUtil.HEADLINE, " Showing callers page " + currentPage + " of " + pager.getTotalPages() + " "));
		header.append(Texts.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
		pager.setHeader(header.build());
		
		// clear the chat
		message.append(FormatUtil.empty());
		message.append(pager.getPage(currentPage));
		callee.sendMessage(message.build());

		return CommandResult.success();

	}

	private CommandResult executeBring(Player caller, Player callee, CommandContext args) {

        plugin.getGame().getEventManager().post(new PlayerBackLocationSaveEvent(caller));
        caller.setRotation(callee.getRotation());
		caller.setLocation(callee.getLocation());

        TextBuilder message = Texts.builder();
        message.append(Texts.of(FormatUtil.DIALOG, "You have been teleported to "));
        message.append(Texts.of(FormatUtil.OBJECT, callee.getName(), FormatUtil.DIALOG, "."));
        caller.sendMessage(message.build());

		return CommandResult.success();
	}

	public static Text getBringAction(String name) {

        return Texts.builder("/bring " + name)
                .onClick(TextActions.runCommand("/bring " + name))
                .onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "/bring ", FormatUtil.OBJECT, name)))
                .color(FormatUtil.GENERIC_LINK).style(TextStyles.UNDERLINE)
                .build();

    }

	public BringCommand(Destinations plugin) {
		this.plugin = plugin;
	}

}
