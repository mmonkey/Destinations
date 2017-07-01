package com.github.mmonkey.destinations.commands;

import com.github.mmonkey.destinations.configs.DestinationsConfig;
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
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.HashSet;
import java.util.Set;

public class SetHomeCommand implements CommandExecutor {

    public static final String[] ALIASES = {"sethome", "addhome"};

    /**
     * Get the Command Specifications for this command
     *
     * @return CommandSpec
     */
    public static CommandSpec getCommandSpec() {
        return CommandSpec.builder()
                .permission("destinations.home.create")
                .description(Text.of("/sethome [-f force] [name]"))
                .extendedDescription(Text.of("Set this location as a home."))
                .executor(new SetHomeCommand())
                .arguments(GenericArguments.flags().flag("f").buildWith(
                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("name")))
                )).build();
    }

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

        int maxHomes = DestinationsConfig.getMaximumHomes();
        if (maxHomes != 0 && homes.size() >= maxHomes) {
            player.sendMessage(MessagesUtil.error(player, "home.max", maxHomes));
            return CommandResult.success();
        }

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
