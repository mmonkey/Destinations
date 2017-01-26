package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.HomeEntity;
import com.github.mmonkey.destinations.entities.LocationEntity;
import com.github.mmonkey.destinations.entities.PlayerEntity;
import com.github.mmonkey.destinations.persistence.cache.PlayerCache;
import com.github.mmonkey.destinations.persistence.repositories.PlayerRepository;
import com.github.mmonkey.destinations.utilities.MessagesUtil;
import com.github.mmonkey.destinations.utilities.PlayerUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashSet;
import java.util.Set;

public class SetHomeCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        boolean force = args.hasAny("f");
        String name = (String) args.getOne("name").orElse("");

        Player player = (Player) src;
        PlayerEntity playerEntity = PlayerCache.instance.get(player);
        Set<HomeEntity> homes = playerEntity.getHomes();
        Set<String> homeNames = new HashSet<>();
        homes.forEach(home -> homeNames.add(home.getName()));

        HomeEntity home = PlayerUtil.getPlayerHomeByName(playerEntity, name);
        if (force && home != null) {
            home.setLocation(new LocationEntity(player));
            playerEntity.getHomes().add(home);
            playerEntity = PlayerRepository.instance.save(playerEntity);
            PlayerCache.instance.set(player, playerEntity);

            player.sendMessage(MessagesUtil.success(player, "home.update", home.getName()));
            return CommandResult.success();
        }

        if (home != null) {
            player.sendMessage(MessagesUtil.error(player, "home.exist", home.getName()));
            return CommandResult.success();
        }

        name = name.isEmpty() ? PlayerUtil.getPlayerHomeAvailableName(playerEntity) : name;
        playerEntity.getHomes().add(new HomeEntity(name, new LocationEntity(player)));
        playerEntity = PlayerRepository.instance.save(playerEntity);
        PlayerCache.instance.set(player, playerEntity);

        player.sendMessage(MessagesUtil.success(player, "home.create", name));
        return CommandResult.success();
    }

}
