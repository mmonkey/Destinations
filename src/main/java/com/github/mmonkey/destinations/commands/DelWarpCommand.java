package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.entities.WarpEntity;
import com.github.mmonkey.destinations.persistence.repositories.WarpRepository;
import com.github.mmonkey.destinations.utilities.FormatUtil;
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

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        boolean cancel = args.hasAny("c");
        boolean force = args.hasAny("f");
        String name = (String) args.getOne("name").orElse("");

        Player player = (Player) src;
        PlayerEntity playerEntity = PlayerUtil.getPlayerEntity(player);
        WarpEntity warp = this.searchWarps(playerEntity, name);

        if (cancel) {
            player.sendMessage(
                    Text.of(FormatUtil.SUCCESS, "Warp ", FormatUtil.OBJECT, name, FormatUtil.SUCCESS, " was not deleted.")
            );
            return CommandResult.success();
        }

        if (force && warp != null) {
            WarpRepository.instance.remove(warp);
            return CommandResult.success();
        }

        if (warp != null) {
            player.sendMessage(
                    Text.of(Text.NEW_LINE).toBuilder()
                            .append(Text.of(FormatUtil.DIALOG, "Are you sure you want to delete warp ", FormatUtil.OBJECT, warp.getName(), FormatUtil.DIALOG,
                                    "?  "))
                            .append(getDeleteWarpConfirmationAction(name))
                            .append(Text.of("  "))
                            .append(getDeleteWarpCancelAction(name))
                            .append(Text.NEW_LINE)
                            .build()
            );
            return CommandResult.success();
        } else {
            player.sendMessage(
                    Text.of(FormatUtil.ERROR, "Warp ", FormatUtil.DELETED_OBJECT, name, FormatUtil.ERROR, " doesn't exist, or you don't have permissions to " +
                            "delete it.")
            );
            return CommandResult.success();
        }

    }

    private WarpEntity searchWarps(PlayerEntity playerEntity, String name) {
        for (WarpEntity warp : PlayerUtil.getPlayerWarps(playerEntity)) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }
        return null;
    }

    private Text getDeleteWarpConfirmationAction(String name) {
        return Text.builder("Yes")
                .onClick(TextActions.runCommand("/delwarp -f " + name))
                .onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Delete warp ", FormatUtil.OBJECT, name)))
                .color(FormatUtil.CONFIRM)
                .style(TextStyles.UNDERLINE)
                .build();
    }

    private Text getDeleteWarpCancelAction(String name) {
        return Text.builder("No")
                .onClick(TextActions.runCommand("/delwarp -c " + name))
                .onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Do not delete warp ", FormatUtil.OBJECT, name)))
                .color(FormatUtil.CANCEL)
                .style(TextStyles.UNDERLINE)
                .build();
    }

}
