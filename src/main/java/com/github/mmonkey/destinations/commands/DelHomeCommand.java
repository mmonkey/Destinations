package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.HomeEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.persistence.repositories.PlayerRepository;
import com.github.mmonkey.destinations.utilities.FormatUtil;
import com.github.mmonkey.destinations.utilities.MessagesUtil;
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
        HomeEntity home = getPlayerHomeByName(playerEntity, name);

        if (cancel) {
            player.sendMessage(MessagesUtil.success(player, "home.delete_cancel", name));
            return CommandResult.success();
        }

        if (force && home != null) {
            playerEntity.getHomes().remove(home);
            playerEntity = PlayerRepository.instance.save(playerEntity);
            PlayerCache.instance.set(player, playerEntity);

            player.sendMessage(MessagesUtil.success(player, "home.delete", home.getName()));
            return CommandResult.success();
        }

        if (home != null) {
            player.sendMessage(
                    Text.builder()
                            .append(Text.of("  "))
                            .append(MessagesUtil.warning(player, "home.delete_confirm", home.getName()))
                            .append(getDeleteHomeConfirmationAction(player, home.getName()))
                            .append(Text.of("  "))
                            .append(getDeleteHomeCancelAction(player, home.getName()))
                            .build()
            );
            return CommandResult.success();
        }

        player.sendMessage(MessagesUtil.error(player, "home.does_not_exist", name));
        return CommandResult.success();

    }

    private Text getDeleteHomeConfirmationAction(Player player, String name) {
        return Text.builder("Yes")
                .onClick(TextActions.runCommand("/delhome -f " + name))
                .onHover(TextActions.showText(MessagesUtil.get(player, "home.delete_confirm_yes", name)))
                .color(FormatUtil.CONFIRM)
                .style(TextStyles.UNDERLINE)
                .build();
    }

    private Text getDeleteHomeCancelAction(Player player, String name) {
        return Text.builder("No")
                .onClick(TextActions.runCommand("/delhome -c " + name))
                .onHover(TextActions.showText(MessagesUtil.get(player, "home.delete_confirm_no", name)))
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
