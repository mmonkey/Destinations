package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.HomeEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.utilities.FormatUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListHomesCommand implements CommandExecutor {

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        Player player = (Player) src;
        PlayerEntity playerEntity = PlayerCache.instance.get(player);

        List<Text> list = new CopyOnWriteArrayList<>();
        Set<HomeEntity> homes = playerEntity.getHomes();
        homes.forEach(home -> list.add(Text.of(getHomeAction(home.getName()), Text.of(" - "), getDeleteHomeAction(home.getName()))));

        if (list.isEmpty()) {
            player.sendMessage(Text.of(FormatUtil.ERROR, "No home has been set!"));
            return CommandResult.success();
        }

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        paginationService.builder().title(Text.of("Homes")).contents(list).padding(Text.of("-")).sendTo(player);
        return CommandResult.success();
    }

    private Text getHomeAction(String name) {
        return Text.builder(name)
                .onClick(TextActions.runCommand("/home " + name))
                .onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Teleport to ", FormatUtil.OBJECT, name)))
                .color(FormatUtil.GENERIC_LINK)
                .style(TextStyles.UNDERLINE)
                .build();
    }

    private Text getDeleteHomeAction(String name) {
        return Text.builder("delete")
                .onClick(TextActions.runCommand("/delhome " + name))
                .onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Delete home ", FormatUtil.OBJECT, name)))
                .color(FormatUtil.DELETE)
                .style(TextStyles.UNDERLINE)
                .build();
    }

}
