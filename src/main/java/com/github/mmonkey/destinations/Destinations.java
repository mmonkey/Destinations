package com.github.mmonkey.destinations;

import com.github.mmonkey.destinations.commands.*;
import com.github.mmonkey.destinations.configs.DestinationsConfig;
import com.github.mmonkey.destinations.entities.*;
import com.github.mmonkey.destinations.listeners.PlayerListeners;
import com.github.mmonkey.destinations.persistence.PersistenceService;
import com.github.mmonkey.destinations.teleportation.TeleportationService;
import com.google.inject.Inject;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;

@Plugin(id = Destinations.ID, name = Destinations.NAME, version = Destinations.VERSION, description = Destinations.DESCRIPTION)
public class Destinations {

    public static final String ID = "destinations";
    public static final String NAME = "destinations";
    public static final String VERSION = "1.0.0";
    public static final String DESCRIPTION = "An all-in-one teleportation plugin, with smart features.";

    private static Destinations instance;

    private DestinationsConfig config;
    private PersistenceService persistenceService;

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    /**
     * Get the plugin instance
     *
     * @return Destinations
     */
    public static Destinations getInstance() {
        return instance;
    }

    /**
     * @return Logger
     */
    public Logger getLogger() {
        return this.logger;
    }

    @Listener
    public void onGamePreInitializationEvent(GamePreInitializationEvent event) {

        instance = this;
        this.logger = LoggerFactory.getLogger(Destinations.NAME);
        getLogger().info(String.format("Starting up %s v%s.", Destinations.NAME, Destinations.VERSION));

        // Load configs
        this.setupConfigs();

        // Setup database
        this.setupDatabase();
    }

    @Listener
    public void onGameInitializationEvent(GameInitializationEvent event) {

        // Register Events
        boolean saveOnBack = config.get().getNode(DestinationsConfig.BACK_SETTINGS, "saveOnDeath").getBoolean();
        Sponge.getEventManager().registerListeners(this, new PlayerListeners(saveOnBack));

        // Register Commands
        this.registerCommands();
    }

    @Listener
    public void onGameReloadEvent(GameReloadEvent event) {

        // Close db sessions
        if (this.persistenceService != null && this.persistenceService.getSessionFactory().isOpen()) {
            this.persistenceService.getSessionFactory().close();
        }

        // Reload configs
        this.setupConfigs();

        // Reload database
        this.setupDatabase();

        this.getLogger().info(String.format("%s was reloaded", Destinations.NAME));
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        if (this.persistenceService != null && this.persistenceService.getSessionFactory().isOpen()) {
            this.persistenceService.getSessionFactory().close();
        }
    }

    /**
     * Setup the configs
     */
    private void setupConfigs() {

        if (!this.configDir.isDirectory() && !this.configDir.mkdirs()) {
            this.getLogger().error(String.format("Unable to create %s config directory, please check file permissions.", Destinations.NAME));
            Sponge.getServer().shutdown();
        }

        try {
            this.config = new DestinationsConfig(this.configDir, "destinations.conf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setup the database
     */
    private void setupDatabase() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(AccessEntity.class);
        configuration.addAnnotatedClass(BackEntity.class);
        configuration.addAnnotatedClass(HomeEntity.class);
        configuration.addAnnotatedClass(LocationEntity.class);
        configuration.addAnnotatedClass(PlayerEntity.class);
        configuration.addAnnotatedClass(WarpEntity.class);
        configuration.addAnnotatedClass(WorldEntity.class);

        String type = this.config.get().getNode(DestinationsConfig.DATABASE_SETTINGS, "type").getString("");
        String url = this.config.get().getNode(DestinationsConfig.DATABASE_SETTINGS, "url").getString("");
        String database = this.config.get().getNode(DestinationsConfig.DATABASE_SETTINGS, "database").getString("");
        String username = this.config.get().getNode(DestinationsConfig.DATABASE_SETTINGS, "username").getString("");
        String password = this.config.get().getNode(DestinationsConfig.DATABASE_SETTINGS, "password").getString("");

        try {
            this.persistenceService = new PersistenceService(configuration, type, url, database, username, password);
        } catch (Exception e) {
            this.getLogger().error(String.format("Please check you database settings in the %s config.", Destinations.NAME));
            Sponge.getServer().shutdown();
        }
    }

    /**
     * Register commands
     */
    private void registerCommands() {

        // TODO: register all commands, then check the enabled flag in each command

        // /home [name]
        CommandSpec homeCommand = CommandSpec.builder()
                .description(Text.of("Teleport Home"))
                .extendedDescription(Text.of("Teleport to the nearest home or to the named home. Optional: /home [name]"))
                .executor(new HomeCommand())
                .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("name"))))
                .build();

        // /sethome [-f] [name]
        CommandSpec setHomeCommand = CommandSpec.builder()
                .description(Text.of("Set location as home"))
                .extendedDescription(Text.of("Set this location as a home. Optional: /sethome [name]"))
                .executor(new SetHomeCommand())
                .arguments(GenericArguments.flags().flag("f").buildWith(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("name")))))
                .build();

        // /listhomes [page]
        CommandSpec listHomesCommand = CommandSpec.builder()
                .description(Text.of("Show list of homes"))
                .extendedDescription(Text.of("Displays a list of your homes. Optional: /listhomes [page]"))
                .executor(new ListHomesCommand())
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("page"))))
                .build();

        // /delhome [-f] [-c] <name>
        CommandSpec delHomeCommand = CommandSpec.builder()
                .description(Text.of("Delete a home"))
                .extendedDescription(Text.of("Delete a home by name. Required: /sethome <name>"))
                .executor(new DelHomeCommand())
                .arguments(GenericArguments.flags().flag("c").flag("f").buildWith(GenericArguments.remainingJoinedStrings(Text.of("name"))))
                .build();

        // Register home commands if enabled
        if (config.get().getNode(DestinationsConfig.HOME_SETTINGS, "enabled").getBoolean()) {
            Sponge.getCommandManager().register(this, homeCommand, "home", "h");
            Sponge.getCommandManager().register(this, setHomeCommand, "sethome");
            Sponge.getCommandManager().register(this, listHomesCommand, "listhomes", "homes");
            Sponge.getCommandManager().register(this, delHomeCommand, "delhome");
        }

        // /warp <name>
        CommandSpec warpCommand = CommandSpec.builder()
                .description(Text.of("Teleport to Warp"))
                .extendedDescription(Text.of("Teleport to the warp of the provided name."))
                .executor(new WarpCommand())
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("name")))
                .build();

        // /setwarp <name>
        CommandSpec setWarpCommand = CommandSpec.builder()
                .description(Text.of("Set a warp"))
                .extendedDescription(Text.of("Set this location as a public warp."))
                .executor(new SetWarpCommand())
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("name")))
                .build();

        // /listwarps [page]
        CommandSpec listWarpsCommand = CommandSpec.builder()
                .description(Text.of("Show list of warps"))
                .extendedDescription(Text.of("Displays a list of your warps. Optional: /listwarps [page]"))
                .executor(new ListWarpsCommand())
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("page"))))
                .build();

        // /delwarp [-f] [-c] <name>
        CommandSpec delWarpCommand = CommandSpec.builder()
                .description(Text.of("Delete a warp"))
                .extendedDescription(Text.of("Delete a warp by name. Required: /delwarp <name>"))
                .executor(new DelWarpCommand())
                .arguments(GenericArguments.flags().flag("c").flag("f").buildWith(GenericArguments.remainingJoinedStrings(Text.of("name"))))
                .build();

        // Register warp commands if enabled
        if (config.get().getNode(DestinationsConfig.WARP_SETTINGS, "enabled").getBoolean()) {
            Sponge.getCommandManager().register(this, warpCommand, "warp", "w");
            Sponge.getCommandManager().register(this, setWarpCommand, "setwarp");
            Sponge.getCommandManager().register(this, listWarpsCommand, "listwarps", "warps");
            Sponge.getCommandManager().register(this, delWarpCommand, "delwarp");
        }

        // /call <player>, /tpa <player>
        CommandSpec callCommand = CommandSpec.builder()
                .description(Text.of("Requests a player to teleport you."))
                .extendedDescription(Text.of("Requests a player to teleport you to their current location. Required: /call <player>"))
                .executor(new CallCommand())
                .arguments(GenericArguments.player(Text.of("player")))
                .build();

        // /bring [player] [page]
        CommandSpec bringCommand = CommandSpec.builder()
                .description(Text.of("Bring a calling player to you."))
                .extendedDescription(Text.of("Teleports a player that has issued a call request to your current location."))
                .executor(new BringCommand())
                .arguments(GenericArguments.optional(GenericArguments.firstParsing(GenericArguments.player(Text.of("player")), GenericArguments.integer(Text
                        .of("page")))))
                .build();

        // /grab <player>, /tphere <player>
        CommandSpec grabCommand = CommandSpec.builder()
                .description(Text.of("Teleport a player to you."))
                .extendedDescription(Text.of("Teleports a player to your current location. Required: /tphere <name>"))
                .executor(new GrabCommand())
                .arguments(GenericArguments.player(Text.of("player")))
                .build();

        // Start teleportation service and register commands
        if (config.get().getNode(DestinationsConfig.TELEPORT_SETTINGS, "enabled").getBoolean(false)) {
            Sponge.getCommandManager().register(this, callCommand, "call", "tpa");
            Sponge.getCommandManager().register(this, bringCommand, "bring");
            Sponge.getCommandManager().register(this, grabCommand, "grab", "tphere");
        }

        // /back
        CommandSpec backCommand = CommandSpec.builder()
                .description(Text.of("Teleport back."))
                .extendedDescription(Text.of("Teleport to your previous location."))
                .executor(new BackCommand())
                .build();

        // Register back command if enabled
        if (config.get().getNode(DestinationsConfig.BACK_SETTINGS, "enabled").getBoolean()) {
            Sponge.getCommandManager().register(this, backCommand, "back", "b");
        }

    }

}
