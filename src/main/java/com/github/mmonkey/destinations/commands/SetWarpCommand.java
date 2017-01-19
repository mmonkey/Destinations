package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.LocationEntity;
import com.github.mmonkey.destinations.entities.WarpEntity;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.persistence.cache.WarpCache;
import com.github.mmonkey.destinations.persistence.repositories.WarpRepository;
import com.github.mmonkey.destinations.utilities.FormatUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class SetWarpCommand implements CommandExecutor {

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        String name = (String) args.getOne("name").orElse("");
        Player player = (Player) src;

        if (warpExists(name)) {
            player.sendMessage(
                    Text.of(FormatUtil.ERROR, "Warp ", FormatUtil.OBJECT, name, FormatUtil.ERROR, " already exists and cannot be added.")
            );
            return CommandResult.success();
        }

        WarpEntity warp = new WarpEntity(name, false, new LocationEntity(player), PlayerCache.instance.get(player));
        warp = WarpRepository.instance.save(warp);
        WarpCache.instance.get().add(warp);

        player.sendMessage(
                Text.of(FormatUtil.SUCCESS, "Warp ", FormatUtil.OBJECT, warp.getName(), FormatUtil.SUCCESS, " was successfully created!")
        );

        // TODO: add private flag

        return CommandResult.success();
    }

    private boolean warpExists(String name) {
        for (WarpEntity warp : WarpCache.instance.get()) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
