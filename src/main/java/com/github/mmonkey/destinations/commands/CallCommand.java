package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.teleportation.TeleportationService;
import com.github.mmonkey.destinations.utilities.MessagesUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CallCommand implements CommandExecutor {

    public static final String[] ALIASES = {"call", "tpa"};

    /**
     * Get the Command Specifications for this command
     *
     * @return CommandSpec
     */
    public static CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("destinations.tpa")
                .description(Text.of("/call <player> or /tpa <player>"))
                .extendedDescription(Text.of("Requests a player to teleport you to their current location."))
                .executor(new CallCommand())
                .arguments(GenericArguments.player(Text.of("player")))
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        Player caller = (Player) src;
        Player target = args.getOne("player").isPresent() ? (Player) args.getOne("player").get() : null;

        if (target == null) {
            caller.sendMessage(MessagesUtil.error(caller, "call.invalid_player"));
            return CommandResult.empty();
        }

        if (TeleportationService.instance.isCalling(caller, target)) {
            caller.sendMessage(MessagesUtil.error(caller, "call.cooldown", target.getName()));
            return CommandResult.success();
        }

        Text.Builder message = Text.builder();
        message.append(MessagesUtil.get(target, "call.request", caller.getName())).append(Text.of(" "));
        message.append(BringCommand.getBringAction(caller.getName()));

        // Send messages
        target.sendMessage(message.build());
        caller.sendMessage(MessagesUtil.success(caller, "call.sent", target.getName()));

        TeleportationService.instance.call(caller, target);
        return CommandResult.success();
    }

}
