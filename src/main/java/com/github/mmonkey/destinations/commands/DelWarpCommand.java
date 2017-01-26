package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.entities.WarpEntity;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.persistence.cache.WarpCache;
import com.github.mmonkey.destinations.persistence.repositories.WarpRepository;
import com.github.mmonkey.destinations.utilities.FormatUtil;
import com.github.mmonkey.destinations.utilities.MessagesUtil;
import com.github.mmonkey.destinations.utilities.PlayerUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;

public class DelWarpCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        boolean cancel = args.hasAny("c");
        boolean force = args.hasAny("f");
        String name = (String) args.getOne("name").orElse("");

        Player player = (Player) src;
        PlayerEntity playerEntity = PlayerCache.instance.get(player);
        WarpEntity warp = this.searchWarps(playerEntity, name);

        if (cancel) {
            player.sendMessage(MessagesUtil.success(player, "warp.delete_cancel", name));
            return CommandResult.success();
        }

        if (force && warp != null) {
            WarpCache.instance.get().remove(warp);
            WarpRepository.instance.remove(warp);

            player.sendMessage(MessagesUtil.success(player, "warp.delete", name));
            return CommandResult.success();
        }

        if (warp != null) {
            player.sendMessage(
                    Text.of(Text.NEW_LINE).toBuilder()
                            .append(Text.of("  "))
                            .append(Text.of(MessagesUtil.get(player, "warp.delete_confirm", name)))
                            .append(getDeleteWarpConfirmationAction(player, name))
                            .append(Text.of("  "))
                            .append(getDeleteWarpCancelAction(player, name))
                            .build()
            );
            return CommandResult.success();
        }

        player.sendMessage(MessagesUtil.error(player, "warp.does_not_exist", name));
        return CommandResult.success();
    }

    private WarpEntity searchWarps(PlayerEntity playerEntity, String name) {
        for (WarpEntity warp : PlayerUtil.getPlayerWarps(playerEntity)) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }
        return null;
    }

    private Text getDeleteWarpConfirmationAction(Player player, String name) {
        return Text.builder("Yes")
                .onClick(TextActions.runCommand("/delwarp -f " + name))
                .onHover(TextActions.showText(MessagesUtil.get(player, "warp.delete_confirm_yes", name)))
                .color(FormatUtil.CONFIRM)
                .style(TextStyles.UNDERLINE)
                .build();
    }

    private Text getDeleteWarpCancelAction(Player player, String name) {
        return Text.builder("No")
                .onClick(TextActions.runCommand("/delwarp -c " + name))
                .onHover(TextActions.showText(MessagesUtil.get(player, "warp.delete_confirm_no", name)))
                .color(FormatUtil.CANCEL)
                .style(TextStyles.UNDERLINE)
                .build();
    }

}
