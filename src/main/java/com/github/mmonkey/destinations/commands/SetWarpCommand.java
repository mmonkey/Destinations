package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.LocationEntity;
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

import java.util.List;

public class SetWarpCommand implements CommandExecutor {

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        String name = (args.hasAny("name")) ? ((String) args.getOne("name").get()) : "";
        Player player = (Player) src;

        if (warpExists(name)) {
            player.sendMessage(
                    Text.of(FormatUtil.ERROR, "Warp ", FormatUtil.OBJECT, name, FormatUtil.ERROR, " already exists and cannot be added.")
            );

            return CommandResult.success();
        }

        WarpEntity warp = new WarpEntity(name, false, new LocationEntity(player), PlayerUtil.getPlayerEntity(player));
        WarpRepository.instance.save(warp);

        player.sendMessage(
                Text.of(FormatUtil.SUCCESS, "Warp ", FormatUtil.OBJECT, warp.getName(), FormatUtil.SUCCESS, " was successfully created!")
        );

        // TODO: add private flag

        return CommandResult.success();
    }

    private boolean warpExists(String name) {
        List<WarpEntity> warps = WarpRepository.instance.getAllWarps();
        for (WarpEntity warp : warps) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
