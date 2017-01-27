package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.events.PlayerTeleportBringEvent;
import com.github.mmonkey.destinations.events.PlayerTeleportPreEvent;
import com.github.mmonkey.destinations.teleportation.TeleportationService;
import com.github.mmonkey.destinations.utilities.FormatUtil;
import com.github.mmonkey.destinations.utilities.MessagesUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BringCommand implements CommandExecutor {

    public static final String[] ALIASES = {"bring", "tpaccept", "tpyes"};

    /**
     * Get the Command Specifications for this command
     *
     * @return CommandSpec
     */
    public static CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("destinations.tpa")
                .description(Text.of("/bring [player] or /tpaccept [player] or /tpyes [player]"))
                .extendedDescription(Text.of("Teleports a player that has issued a call request to your current location."))
                .executor(new BringCommand())
                .arguments(GenericArguments.optional(GenericArguments.firstParsing(GenericArguments.player(Text.of("player")))))
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        Player target = (Player) src;
        Player caller = args.getOne("player").isPresent() ? (Player) args.getOne("player").get() : null;

        if (caller == null) {

            int numCallers = TeleportationService.instance.getNumCallers(target);

            switch (numCallers) {
                case 0:
                    target.sendMessage(MessagesUtil.error(target, "bring.empty"));
                    return CommandResult.success();

                case 1:
                    Player calling = TeleportationService.instance.getFirstCaller(target);
                    return executeBring(calling, target);

                default:
                    return listCallers(target, args);
            }
        }

        if (TeleportationService.instance.isCalling(caller, target)) {
            return executeBring(caller, target);
        }

        target.sendMessage(MessagesUtil.error(target, "bring.not_found", caller.getName()));
        return CommandResult.success();
    }

    private CommandResult listCallers(Player target, CommandContext args) {
        List<String> callList = TeleportationService.instance.getCalling(target);
        List<Text> list = new CopyOnWriteArrayList<>();
        callList.forEach(caller -> list.add(getBringAction(caller)));

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        paginationService.builder().title(MessagesUtil.get(target, "bring.title")).contents(list).padding(Text.of("-")).sendTo(target);
        return CommandResult.success();
    }

    private CommandResult executeBring(Player caller, Player target) {
        TeleportationService.instance.removeCall(caller, target);
        Sponge.getGame().getEventManager().post(new PlayerTeleportPreEvent(caller, caller.getLocation(), caller.getRotation()));
        Sponge.getGame().getEventManager().post(new PlayerTeleportBringEvent(caller, target.getLocation(), target.getRotation()));

        caller.sendMessage(MessagesUtil.success(caller, "bring.teleport", target.getName()));
        return CommandResult.success();
    }

    static Text getBringAction(String name) {
        return Text.builder("/bring " + name)
                .onClick(TextActions.runCommand("/bring " + name))
                .onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "/bring ", FormatUtil.OBJECT, name)))
                .color(FormatUtil.GENERIC_LINK).style(TextStyles.UNDERLINE)
                .build();

    }

}
