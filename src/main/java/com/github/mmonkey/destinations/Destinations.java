package com.github.mmonkey.destinations;

import com.github.mmonkey.destinations.commands.*;
import com.github.mmonkey.destinations.configs.DestinationsConfig;
import com.github.mmonkey.destinations.entities.*;
import com.github.mmonkey.destinations.listeners.PlayerListeners;
import com.github.mmonkey.destinations.listeners.TeleportListeners;
import com.github.mmonkey.destinations.persistence.PersistenceService;
import com.github.mmonkey.destinations.persistence.cache.SpawnCache;
import com.github.mmonkey.destinations.persistence.cache.WarpCache;
import com.github.mmonkey.destinations.persistence.repositories.SpawnRepository;
import com.github.mmonkey.destinations.persistence.repositories.WarpRepository;
import com.google.inject.Inject;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

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
    private boolean loaded = false;

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
        if (this.setupConfigs() && this.setupDatabase()) {
            this.load();
            this.getLogger().info(String.format("%s loaded successfully.", Destinations.NAME));
        } else {
            this.getLogger().error(String.format("%s has failed to load.", Destinations.NAME));
        }
    }

    @Listener
    public void onGameInitializationEvent(GameInitializationEvent event) {
        if (this.loaded) {
            this.registerListeners();
            this.registerCommands();
        }
    }

    @Listener
    public void onGameReloadEvent(GameReloadEvent event) {

        // Close db sessions
        if (this.persistenceService != null && this.persistenceService.getSessionFactory().isOpen()) {
            this.persistenceService.getSessionFactory().close();
        }

        if (this.setupConfigs() && this.setupDatabase()) {
            if (!this.loaded) {
                this.load();
                this.registerListeners();
                this.registerCommands();
            }
            this.getLogger().info(String.format("%s was reloaded.", Destinations.NAME));
        } else {
            this.getLogger().error(String.format("There was an error reloading %s.", Destinations.NAME));
        }
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
    private boolean setupConfigs() {

        if (!this.configDir.isDirectory() && !this.configDir.mkdirs()) {
            this.getLogger().error(String.format("Unable to create %s config directory, please check file permissions.", Destinations.NAME));
            return false;
        }

        try {
            this.config = new DestinationsConfig(this.configDir, "destinations.conf");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Setup the database
     */
    private boolean setupDatabase() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(AccessEntity.class);
        configuration.addAnnotatedClass(BackEntity.class);
        configuration.addAnnotatedClass(BedEntity.class);
        configuration.addAnnotatedClass(HomeEntity.class);
        configuration.addAnnotatedClass(LocationEntity.class);
        configuration.addAnnotatedClass(PlayerEntity.class);
        configuration.addAnnotatedClass(SpawnEntity.class);
        configuration.addAnnotatedClass(WarpEntity.class);
        configuration.addAnnotatedClass(WorldEntity.class);

        String type = this.config.get().getNode(DestinationsConfig.DATABASE_SETTINGS, "type").getString("");
        String url = this.config.get().getNode(DestinationsConfig.DATABASE_SETTINGS, "url").getString("");
        String database = this.config.get().getNode(DestinationsConfig.DATABASE_SETTINGS, "database").getString("");
        String username = this.config.get().getNode(DestinationsConfig.DATABASE_SETTINGS, "username").getString("");
        String password = this.config.get().getNode(DestinationsConfig.DATABASE_SETTINGS, "password").getString("");

        try {
            this.persistenceService = new PersistenceService(configuration, type, url, database, username, password);
            return true;
        } catch (Exception e) {
            this.getLogger().error(String.format("Please check you database settings in the %s config.", Destinations.NAME));
            return false;
        }
    }

    /**
     * Load data into cache
     */
    private void load() {

        // Load spawns into cache
        SpawnCache.instance.get().addAll(SpawnRepository.instance.getAllSpawns());

        // Load warps into cache
        WarpCache.instance.get().addAll(WarpRepository.instance.getAllWarps());

        this.loaded = true;
    }

    /**
     * Register event listeners
     */
    private void registerListeners() {
        Sponge.getEventManager().registerListeners(this, new PlayerListeners());
        Sponge.getEventManager().registerListeners(this, new TeleportListeners());
    }

    /**
     * Register commands
     */
    private void registerCommands() {
        CommandManager commandManager = Sponge.getCommandManager();

        // General Commands
        commandManager.register(this, BackCommand.getCommandSpec(), BackCommand.ALIASES);
        commandManager.register(this, BedCommand.getCommandSpec(), BedCommand.ALIASES);
        commandManager.register(this, JumpCommand.getCommandSpec(), JumpCommand.ALIASES);
        commandManager.register(this, TopCommand.getCommandSpec(), TopCommand.ALIASES);

        // Home Commands
        commandManager.register(this, DelHomeCommand.getCommandSpec(), DelHomeCommand.ALIASES);
        commandManager.register(this, HomeCommand.getCommandSpec(), HomeCommand.ALIASES);
        commandManager.register(this, ListHomesCommand.getCommandSpec(), ListHomesCommand.ALIASES);
        commandManager.register(this, SetHomeCommand.getCommandSpec(), SetHomeCommand.ALIASES);

        // Spawn Commands
        commandManager.register(this, SetSpawnCommand.getCommandSpec(), SetSpawnCommand.ALIASES);
        commandManager.register(this, SpawnCommand.getCommandSpec(), SpawnCommand.ALIASES);

        // Teleport Commands
        commandManager.register(this, BringCommand.getCommandSpec(), BringCommand.ALIASES);
        commandManager.register(this, CallCommand.getCommandSpec(), CallCommand.ALIASES);
        commandManager.register(this, GrabCommand.getCommandSpec(), GrabCommand.ALIASES);

        // Warp Commands
        commandManager.register(this, DelWarpCommand.getCommandSpec(), DelWarpCommand.ALIASES);
        commandManager.register(this, ListWarpsCommand.getCommandSpec(), ListWarpsCommand.ALIASES);
        commandManager.register(this, SetWarpCommand.getCommandSpec(), SetWarpCommand.ALIASES);
        commandManager.register(this, WarpCommand.getCommandSpec(), WarpCommand.ALIASES);
    }

}
