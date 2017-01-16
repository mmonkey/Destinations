package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.HomeEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.persistence.repositories.PlayerRepository;
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

public class DelHomeCommand implements CommandExecutor {

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        boolean force = (args.hasAny("f")) ? (Boolean) args.getOne("f").get() : false;
        boolean cancel = (args.hasAny("c")) ? (Boolean) args.getOne("c").get() : false;
        String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";

        Player player = (Player) src;
        PlayerEntity playerEntity = PlayerUtil.getPlayerEntity(player);
        HomeEntity home = getPlayerHomeByName(playerEntity, name);

        if (cancel) {
            player.sendMessage(
                    Text.of(FormatUtil.SUCCESS, "Home ", FormatUtil.OBJECT, name, FormatUtil.SUCCESS, " was not deleted.")
            );
            return CommandResult.success();
        }

        if (force && home != null) {
            playerEntity.getHomes().remove(home);
            PlayerRepository.instance.save(playerEntity);
            player.sendMessage(
                    Text.of(FormatUtil.empty(), FormatUtil.SUCCESS, "Home ", FormatUtil.DELETED_OBJECT, home.getName(), FormatUtil.SUCCESS, " was " +
                            "successfully deleted!")
            );
            return CommandResult.success();
        }

        if (home != null) {

            player.sendMessage(
                    Text.builder()
                            .append(Text.NEW_LINE)
                            .append(Text.of(FormatUtil.DIALOG, "Are you sure you want to delete home ", FormatUtil.OBJECT, name, FormatUtil.DIALOG, "?  "))
                            .append(getDeleteHomeConfirmationAction(name))
                            .append(Text.of("  "))
                            .append(getDeleteHomeCancelAction(name))
                            .append(Text.NEW_LINE)
                            .build()
            );

            return CommandResult.success();

        } else {

            player.sendMessage(
                    Text.of(FormatUtil.ERROR, "Home ", FormatUtil.DELETED_OBJECT, name, FormatUtil.ERROR, " doesn't exist.")
            );

            return CommandResult.success();

        }

    }

    private Text getDeleteHomeConfirmationAction(String name) {

        return Text.builder("Yes")
                .onClick(TextActions.runCommand("/delhome -f " + name))
                .onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Delete home ", FormatUtil.OBJECT, name)))
                .color(FormatUtil.CONFIRM)
                .style(TextStyles.UNDERLINE)
                .build();

    }

    private Text getDeleteHomeCancelAction(String name) {

        return Text.builder("No")
                .onClick(TextActions.runCommand("/delhome -c " + name))
                .onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Do not delete home ", FormatUtil.OBJECT, name)))
                .color(FormatUtil.CANCEL)
                .style(TextStyles.UNDERLINE)
                .build();

    }

    private HomeEntity getPlayerHomeByName(PlayerEntity player, String name) {
        HomeEntity result = null;
        for (HomeEntity home : player.getHomes()) {
            if (home.getName().equals(name)) {
                result = home;
            }
        }
        return result;
    }

}
