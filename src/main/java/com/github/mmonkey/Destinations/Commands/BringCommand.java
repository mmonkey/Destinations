package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Events.PlayerBackLocationSaveEvent;
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

import java.util.List;

public class BringCommand implements CommandExecutor {

	private Destinations plugin;

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			return CommandResult.empty();
		}

        Player target = (Player) src;
		Player caller = args.getOne("player").isPresent() ? (Player) args.getOne("player").get() : null;
		
		if (caller == null) {

            int numCallers = plugin.getCallService().getNumCallers(target);

            switch (numCallers) {
                case 0:
                    target.sendMessage(Text.of(FormatUtil.ERROR, "You have no call requests."));
                    return CommandResult.success();

                case 1:
                    Player calling = plugin.getCallService().getFirstCaller(target);
                    return executeBring(calling, target);

                default:
                    return listCallers(target, args);
            }
        }

        if (plugin.getCallService().isCalling(caller, target)) {

            return executeBring(caller, target);

        } else {

            Text.Builder message = Text.builder();
            message.append(Text.of(FormatUtil.WARN, "You have no active requests from "));
            message.append(Text.of(FormatUtil.OBJECT, caller.getName()));
            message.append(Text.of(FormatUtil.WARN, "."));

            target.sendMessage(message.build());
            return CommandResult.success();
        }

	}

	private CommandResult listCallers(Player target, CommandContext args) {

		List<String> callList = plugin.getCallService().getCalling(target);
        int currentPage = (args.hasAny("page")) ? (Integer) args.getOne("page").get() : 1;

        Text.Builder header = Text.builder();
        Text.Builder message = Text.builder();
		PaginatedList paginatedList = new PaginatedList("/bring");

		for (String name : callList) {
			Text.Builder row = Text.builder();
			row.append(getBringAction(name));
            paginatedList.add(row.build());
		}

        currentPage = currentPage > paginatedList.getTotalPages() ? paginatedList.getTotalPages() : currentPage;

		header.append(Text.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
		header.append(Text.of(FormatUtil.HEADLINE, " Showing callers page " + currentPage + " of " + paginatedList.getTotalPages() + " "));
		header.append(Text.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
        paginatedList.setHeader(header.build());
		
		// clear the chat
		message.append(FormatUtil.empty());
		message.append(paginatedList.getPage(currentPage));
		target.sendMessage(message.build());

		return CommandResult.success();

	}

	private CommandResult executeBring(Player caller, Player target) {

        plugin.getCallService().removeCall(caller, target);
        plugin.getGame().getEventManager().post(new PlayerBackLocationSaveEvent(caller));
        caller.setRotation(target.getRotation());
		caller.setLocation(target.getLocation());

        Text.Builder message = Text.builder();
        message.append(Text.of(FormatUtil.DIALOG, "You have been teleported to "));
        message.append(Text.of(FormatUtil.OBJECT, target.getName(), FormatUtil.DIALOG, "."));
        caller.sendMessage(message.build());

		return CommandResult.success();
	}

	public static Text getBringAction(String name) {

        return Text.builder("/bring " + name)
                .onClick(TextActions.runCommand("/bring " + name))
                .onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "/bring ", FormatUtil.OBJECT, name)))
                .color(FormatUtil.GENERIC_LINK).style(TextStyles.UNDERLINE)
                .build();

    }

	public BringCommand(Destinations plugin) {
		this.plugin = plugin;
	}

}
