package com.github.mmonkey.Destinations.Commands;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Services.CallService;
import com.google.common.base.Optional;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

public class CallCommand implements CommandExecutor {
    private final Destinations plugin;
    private final CallService callService;

    public CallCommand(Destinations plugin, CallService callService) {
        this.plugin = plugin;
        this.callService = callService;
    }

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        final Player srcAsPlayer = (Player) src;
        final String srcName = srcAsPlayer.getName();


        Optional<Player> playerOptional = args.getOne("call-ee");
        if(!playerOptional.isPresent()){
            return CommandResult.empty();
        }

        final Player player = playerOptional.get();

        callService.call(player, srcAsPlayer);
        src.sendMessage(Texts.of(player.getName() + " was called."));
        final Text callText = Texts.builder(srcAsPlayer.getName() + " has requested a teleport type ")
                .append(BringCommand.getBringAction(srcName))
                .append(Texts.of(" to teleport them to you."))
                .build();
        player.sendMessage(callText);
        return CommandResult.success();
    }
}
