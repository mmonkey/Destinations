package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.AccessEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.entities.WarpEntity;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.utilities.FormatUtil;
import com.github.mmonkey.destinations.utilities.PlayerUtil;
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

public class ListWarpsCommand implements CommandExecutor {

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        Player player = (Player) src;
        PlayerEntity playerEntity = PlayerCache.instance.get(player);
        Set<WarpEntity> warps = PlayerUtil.getPlayerWarps(playerEntity);

        if (warps.size() == 0) {
            player.sendMessage(
                    Text.of(FormatUtil.ERROR, "No warps have been set.")
            );
            return CommandResult.success();
        }

        List<Text> list = new CopyOnWriteArrayList<>();
        warps.forEach(warp -> list.add(Text.of(getWarpAction(warp), getDeleteWarpAction(warp, playerEntity))));

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        paginationService.builder().title(Text.of("Warps")).contents(list).padding(Text.of("-")).sendTo(player);
        return CommandResult.success();
    }

    private Text getWarpAction(WarpEntity warp) {

        if (!warp.isPrivate()) {
            return Text.builder(warp.getName())
                    .onClick(TextActions.runCommand("/warp " + warp.getName()))
                    .onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Teleport to ", FormatUtil.OBJECT, warp.getName())))
                    .color(FormatUtil.GENERIC_LINK)
                    .style(TextStyles.UNDERLINE)
                    .build();

        } else {
            return Text.builder(warp.getName() + " (private)")
                    .onClick(TextActions.runCommand("/warp " + warp.getName()))
                    .onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Teleport to ", FormatUtil.OBJECT, warp.getName())))
                    .color(FormatUtil.GENERIC_LINK)
                    .style(TextStyles.UNDERLINE)
                    .build();
        }
    }

    private Text getDeleteWarpAction(WarpEntity warp, PlayerEntity player) {
        boolean canAdministrate = false;
        if (warp.getOwner().getIdentifier().equals(player.getIdentifier())) {
            canAdministrate = true;
        } else {
            for (AccessEntity access : warp.getAccess()) {
                if (access.getPlayer().getIdentifier().equals(player.getIdentifier()) && access.isCanAdministrate()) {
                    canAdministrate = true;
                }
            }
        }

        if (canAdministrate) {
            Text.Builder deleteAction = Text.builder();
            deleteAction.append(Text.of(" - "));
            deleteAction.append(Text.builder("delete")
                    .onClick(TextActions.runCommand("/delwarp " + warp.getName()))
                    .onHover(TextActions.showText(Text.of(FormatUtil.DIALOG, "Delete warp ", FormatUtil.OBJECT, warp.getName())))
                    .color(FormatUtil.DELETE)
                    .style(TextStyles.UNDERLINE)
                    .build());

            return deleteAction.build();
        }
        return Text.of("");
    }

}
