package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Pagination.PaginatedList;
import com.github.mmonkey.Destinations.Pagination.PaginatedListUtil;
import com.github.mmonkey.Destinations.Services.CallService;
import com.github.mmonkey.Destinations.Utilities.FormatUtil;
import com.google.common.base.Optional;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.User;
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
import java.util.Set;

public class BringCommand implements CommandExecutor {
    private final Destinations plugin;
    private final CallService callService;

    public BringCommand(Destinations plugin, CallService callService) {
        this.plugin = plugin;
        this.callService = callService;
    }

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }
        final Optional<Player> playerOptional = args.getOne("caller");
        final Player srcAsPlayer = (Player) src;
        if (playerOptional.isPresent()) {
            return executeBring(playerOptional.get(), srcAsPlayer, args);
        }
        else {
           return listCallers(srcAsPlayer, args);
        }
    }

    private CommandResult listCallers(Player src, CommandContext args) {
        final List<String> callList = callService.getCalling(src);
        final PaginatedList pager = new PaginatedList("/bring");
        final int currentPage = (args.hasAny("page")) ? (Integer) args.getOne("page").get() : 1;

        for (String name: callList) {

            TextBuilder row = Texts.builder();
            row.append(getBringAction(name));
            pager.add(row.build());
        }

        final TextBuilder header = Texts.builder();
        final TextBuilder message = Texts.builder();

        header.append(Texts.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
        header.append(Texts.of(FormatUtil.HEADLINE, " Showing homes page " + currentPage + " of " + pager.getTotalPages() + " "));
        header.append(Texts.of(FormatUtil.HEADLINE, FormatUtil.getFill(12, '-')));
        pager.setHeader(header.build());
        //clear the chat
        message.append(FormatUtil.empty());
        message.append(pager.getPage(currentPage));
        src.sendMessage(message.build());

        return CommandResult.success();

    }

    private CommandResult executeBring(Player player, Player src, CommandContext args) {
        player.setLocation(src.getLocation());
        player.sendMessage(Texts.of("You have been teleported to "+src.getName()));
        return CommandResult.success();
    }

    static Text getBringAction(String name) {
        return Texts.builder("/bring " + name)
                .onClick(TextActions.runCommand("/bring " + name))
                .onHover(TextActions.showText(Texts.of(FormatUtil.DIALOG, "/bring ", FormatUtil.OBJECT, name)))
                .color(FormatUtil.GENERIC_LINK)
                .style(TextStyles.UNDERLINE)
                .build();
    }
}
