package com.github.mmonkey.destinations;

import com.github.mmonkey.destinations.commands.*;
import com.github.mmonkey.destinations.commands.elements.HomeCommandElement;
import com.github.mmonkey.destinations.commands.elements.WarpCommandElement;
import com.github.mmonkey.destinations.configs.DestinationsConfig;
import com.github.mmonkey.destinations.entities.*;
import com.github.mmonkey.destinations.listeners.PlayerListeners;
import com.github.mmonkey.destinations.listeners.TeleportListeners;
import com.github.mmonkey.destinations.persistence.PersistenceService;
import com.github.mmonkey.destinations.persistence.cache.WarpCache;
import com.github.mmonkey.destinations.persistence.repositories.WarpRepository;
import com.google.inject.Inject;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
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
    public static final String NAME = "Destinations";
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

        // Load data
        this.load();
    }

    @Listener
    public void onGameInitializationEvent(GameInitializationEvent event) {

        // Register Events
        Sponge.getEventManager().registerListeners(this, new PlayerListeners());
        Sponge.getEventManager().registerListeners(this, new TeleportListeners());

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
        configuration.addAnnotatedClass(BedEntity.class);
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
     * Load data into cache
     */
    private void load() {

        // Load warps into cache
        WarpCache.instance.get().addAll(WarpRepository.instance.getAllWarps());
    }

    /**
     * Register commands
     */
    private void registerCommands() {

        CommandManager commandManager = Sponge.getCommandManager();

        // /jump
        CommandSpec jumpCommand = CommandSpec.builder()
                .permission("destinations.jump")
                .description(Text.of("/jump"))
                .extendedDescription(Text.of("Teleports the player where they are looking."))
                .executor(new JumpCommand())
                .build();
        commandManager.register(this, jumpCommand, "jump", "j");

        // /top
        CommandSpec topCommand = CommandSpec.builder()
                .permission("destinations.top")
                .description(Text.of("/top"))
                .extendedDescription(Text.of("Teleports the player to the highest block at current location."))
                .executor(new TopCommand())
                .build();
        commandManager.register(this, topCommand, "top");

        // /bed
        CommandSpec bedCommand = CommandSpec.builder()
                .permission("destinations.bed")
                .description(Text.of("/bed"))
                .extendedDescription(Text.of("Teleports the player to the last bed they used."))
                .executor(new BedCommand())
                .build();
        commandManager.register(this, bedCommand, "bed");

        // Register Back Command
        if (DestinationsConfig.isBackCommandEnabled()) {

            // /back
            CommandSpec backCommand = CommandSpec.builder()
                    .permission("destinations.back")
                    .description(Text.of("/back"))
                    .extendedDescription(Text.of("Returns you to your last position from a prior teleport."))
                    .executor(new BackCommand())
                    .build();
            commandManager.register(this, backCommand, "back", "b");
        }

        // Register Home Commands
        if (DestinationsConfig.isHomeCommandEnabled()) {

            // /home [name]
            CommandSpec homeCommand = CommandSpec.builder()
                    .permission("destinations.home")
                    .description(Text.of("/home [name]"))
                    .extendedDescription(Text.of("Teleport to the nearest home or to the named home."))
                    .executor(new HomeCommand())
                    .arguments(GenericArguments.optional(new HomeCommandElement(Text.of("name"))))
                    .build();
            commandManager.register(this, homeCommand, "home", "h");

            // /sethome [-f] [name]
            CommandSpec setHomeCommand = CommandSpec.builder()
                    .permission("destinations.home")
                    .description(Text.of("/sethome [-f force] [name]"))
                    .extendedDescription(Text.of("Set this location as a home."))
                    .executor(new SetHomeCommand())
                    .arguments(GenericArguments.flags().flag("f").buildWith(
                            GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("name")))
                    )).build();
            commandManager.register(this, setHomeCommand, "sethome");

            // /homes
            CommandSpec listHomesCommand = CommandSpec.builder()
                    .permission("destinations.home")
                    .description(Text.of("/homes or /listhomes"))
                    .extendedDescription(Text.of("Displays a list of your homes."))
                    .executor(new ListHomesCommand())
                    .build();
            commandManager.register(this, listHomesCommand, "homes", "listhomes");

            // /delhome [-f] [-c] <name>
            CommandSpec delHomeCommand = CommandSpec.builder()
                    .permission("destinations.home")
                    .description(Text.of("/delhome [-c cancel] [-f force] <name>"))
                    .extendedDescription(Text.of("Delete a home by name."))
                    .executor(new DelHomeCommand())
                    .arguments(GenericArguments.flags().flag("c").flag("f").buildWith(GenericArguments.remainingJoinedStrings(Text.of("name"))))
                    .build();
            commandManager.register(this, delHomeCommand, "delhome");
        }

        // Register Warp Commands
        if (DestinationsConfig.isWarpCommandEnabled()) {

            // /warp <name>
            CommandSpec warpCommand = CommandSpec.builder()
                    .permission("destinations.warp.use")
                    .description(Text.of("/warp <name>"))
                    .extendedDescription(Text.of("Teleport to the warp of the provided name."))
                    .executor(new WarpCommand())
                    .arguments(new WarpCommandElement(Text.of("name")))
                    .build();
            commandManager.register(this, warpCommand, "warp", "w");

            // /warps
            CommandSpec listWarpsCommand = CommandSpec.builder()
                    .permission("destionations.warp.use")
                    .description(Text.of("/warps or /listwarps"))
                    .extendedDescription(Text.of("Displays a list of your warps."))
                    .executor(new ListWarpsCommand())
                    .build();
            commandManager.register(this, listWarpsCommand, "warps", "listwarps");

            // /setwarp <name>
            CommandSpec setWarpCommand = CommandSpec.builder()
                    .permission("destinations.warp.create")
                    .description(Text.of("/setwarp <name>"))
                    .extendedDescription(Text.of("Set this location as a public warp."))
                    .executor(new SetWarpCommand())
                    .arguments(GenericArguments.remainingJoinedStrings(Text.of("name")))
                    .build();
            commandManager.register(this, setWarpCommand, "setwarp");

            // /delwarp [-f] [-c] <name>
            CommandSpec delWarpCommand = CommandSpec.builder()
                    .permission("destinations.warp.create")
                    .description(Text.of("Delete a warp"))
                    .extendedDescription(Text.of("Delete a warp by name."))
                    .executor(new DelWarpCommand())
                    .arguments(GenericArguments.flags().flag("c").flag("f").buildWith(GenericArguments.remainingJoinedStrings(Text.of("name"))))
                    .build();
            commandManager.register(this, delWarpCommand, "delwarp");
        }

        // Register Teleport Commands
        if (DestinationsConfig.isTeleportCommandEnabled()) {

            // /call <player> or /tpa <player>
            CommandSpec callCommand = CommandSpec.builder()
                    .permission("destinations.tpa")
                    .description(Text.of("/call <player> or /tpa <player>"))
                    .extendedDescription(Text.of("Requests a player to teleport you to their current location."))
                    .executor(new CallCommand())
                    .arguments(GenericArguments.player(Text.of("player")))
                    .build();
            commandManager.register(this, callCommand, "call", "tpa");

            // /bring [player] or /tpaccept [player]
            CommandSpec bringCommand = CommandSpec.builder()
                    .permission("destinations.tpa")
                    .description(Text.of("/bring [player] or /tpaccept [player] or /tpyes [player]"))
                    .extendedDescription(Text.of("Teleports a player that has issued a call request to your current location."))
                    .executor(new BringCommand())
                    .arguments(GenericArguments.optional(GenericArguments.firstParsing(GenericArguments.player(Text.of("player")))))
                    .build();
            commandManager.register(this, bringCommand, "bring", "tpaccept");

            // /grab <player> or /tphere <player>
            CommandSpec grabCommand = CommandSpec.builder()
                    .permission("destinations.tphere")
                    .description(Text.of("/grab <player> or /tphere <player>"))
                    .extendedDescription(Text.of("Teleports a player to your current location."))
                    .executor(new GrabCommand())
                    .arguments(GenericArguments.player(Text.of("player")))
                    .build();
            commandManager.register(this, grabCommand, "grab", "tphere");
        }
    }

}
