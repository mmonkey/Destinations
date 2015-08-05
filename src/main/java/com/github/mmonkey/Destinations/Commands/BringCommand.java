package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Events.PlayerBackLocationSaveEvent;
import com.github.mmonkey.Destinations.Pagination.PaginatedList;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
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
		Player caller = args.getOne("player").isPresent() ? (Player) args.getOne("player").get() : null;
		
		if (caller == null) {

            int numCallers = plugin.getCallService().getNumCallers(callee);

            switch (numCallers) {
                case 0:
                    callee.sendMessage(Texts.of(FormatUtil.ERROR, "You have no call requests."));
                    return CommandResult.success();

                case 1:
                    Player calling = (Player) plugin.getCallService().getFirstCaller(callee);
                    return executeBring(calling, callee);

                default:
                    return listCallers(callee, args);
            }
        }

        if (plugin.getCallService().isCalling(caller, callee)) {

            return executeBring(caller, callee);

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
        int currentPage = (args.hasAny("page")) ? (Integer) args.getOne("page").get() : 1;

        TextBuilder header = Texts.builder();
        TextBuilder message = Texts.builder();
		PaginatedList paginatedList = new PaginatedList("/bring");

		for (String name : callList) {
			TextBuilder row = Texts.builder();
			row.append(getBringAction(name));
            paginatedList.add(row.build());
		}

        currentPage = currentPage > paginatedList.getTotalPages() ? paginatedList.getTotalPages() : currentPage;

		header.append(Texts.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
		header.append(Texts.of(FormatUtil.HEADLINE, " Showing callers page " + currentPage + " of " + paginatedList.getTotalPages() + " "));
		header.append(Texts.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
        paginatedList.setHeader(header.build());
		
		// clear the chat
		message.append(FormatUtil.empty());
		message.append(paginatedList.getPage(currentPage));
		callee.sendMessage(message.build());

		return CommandResult.success();

	}

	private CommandResult executeBring(Player caller, Player callee) {

        plugin.getCallService().removeCall(caller, callee);
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
