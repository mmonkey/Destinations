package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.AccessEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.entities.WarpEntity;
import com.github.mmonkey.destinations.events.PlayerBackLocationSaveEvent;
import com.github.mmonkey.destinations.persistence.repositories.WarpRepository;
import com.github.mmonkey.destinations.utilities.FormatUtil;
import com.github.mmonkey.destinations.utilities.PlayerUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;

public class WarpCommand implements CommandExecutor {

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        String name = (String) args.getOne("name").orElse("");

        Player player = (Player) src;
        PlayerEntity playerEntity = PlayerUtil.getPlayerEntity(player);
        WarpEntity warp = this.searchWarps(name);

        if (warp == null) {
            player.sendMessage(
                    Text.of(FormatUtil.ERROR, "Warp ", FormatUtil.OBJECT, name, FormatUtil.ERROR, " does not exist.")
            );
            return CommandResult.success();
        }

        if (warp.isPrivate() && !warp.getOwner().getIdentifier().equals(playerEntity.getIdentifier())) {
            boolean hasAccess = false;
            for (AccessEntity access : warp.getAccess()) {
                if (access.getPlayer().getIdentifier().equals(playerEntity.getIdentifier())) {
                    hasAccess = true;
                }
            }

            if (!hasAccess) {
                player.sendMessage(
                        Text.of(FormatUtil.ERROR, "You do not have access to warp: ", FormatUtil.OBJECT, name, FormatUtil.ERROR, ".")
                );
                return CommandResult.success();
            }
        }

        Sponge.getEventManager().post(new PlayerBackLocationSaveEvent(player));
        player.setLocationAndRotationSafely(warp.getLocation().getLocation(), warp.getLocation().getRotation());
        return CommandResult.success();
    }

    private WarpEntity searchWarps(String name) {
        List<WarpEntity> warps = WarpRepository.instance.getAllWarps();
        for (WarpEntity warp : warps) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }
        return null;
    }

}
