package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.entities.HomeEntity;
import com.github.mmonkey.destinations.entities.LocationEntity;
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

import java.util.HashSet;
import java.util.Set;

public class SetHomeCommand implements CommandExecutor {

    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            return CommandResult.empty();
        }

        boolean force = args.hasAny("f");
        String name = (String) args.getOne("name").orElse("");

        Player player = (Player) src;
        PlayerEntity playerEntity = PlayerUtil.getPlayerEntity(player);
        Set<HomeEntity> homes = playerEntity.getHomes();
        Set<String> homeNames = new HashSet<>();
        homes.forEach(home -> homeNames.add(home.getName()));

        HomeEntity home = PlayerUtil.getPlayerHomeByName(playerEntity, name);
        if (force && home != null) {
            home.setLocation(new LocationEntity(player));
            playerEntity.getHomes().add(home);
            PlayerRepository.instance.save(playerEntity);
            player.sendMessage(
                    Text.of(FormatUtil.SUCCESS, "Home ", FormatUtil.OBJECT, home.getName(), FormatUtil.SUCCESS, " has been updated to this location")
            );
            return CommandResult.success();
        }

        if (home != null) {
            player.sendMessage(
                    Text.of(FormatUtil.ERROR, "Home ", FormatUtil.OBJECT, name, FormatUtil.ERROR, " already exists!")
            );
            return CommandResult.success();
        }

        name = name.isEmpty() ? PlayerUtil.getPlayerHomeAvailableName(playerEntity) : name;
        playerEntity.getHomes().add(new HomeEntity(name, new LocationEntity(player)));
        PlayerRepository.instance.save(playerEntity);

        player.sendMessage(
                Text.of(FormatUtil.SUCCESS, "Home ", FormatUtil.OBJECT, name, FormatUtil.SUCCESS, " was successfully created!")
        );

        return CommandResult.success();

    }

}
