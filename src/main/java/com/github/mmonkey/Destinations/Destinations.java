package com.github.mmonkey.Destinations;

import com.github.mmonkey.Destinations.Commands.*;
import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Dams.TestConnectionDam;
import com.github.mmonkey.Destinations.Database.Database;
import com.github.mmonkey.Destinations.Database.H2EmbeddedDatabase;
import com.github.mmonkey.Destinations.Listeners.BackListener;
import com.github.mmonkey.Destinations.Listeners.ConvertTextListener;
import com.github.mmonkey.Destinations.Listeners.DeathListener;
import com.github.mmonkey.Destinations.Migrations.ConfigMigrationRunner;
import com.github.mmonkey.Destinations.Migrations.DatabaseMigrationRunner;
import com.github.mmonkey.Destinations.Services.CallService;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.File;

@Plugin(id = Destinations.ID, name = Destinations.NAME, version = Destinations.VERSION)
public class Destinations {

    public static final String NAME = "Destinations";
    public static final String ID = "Destinations";
    public static final String VERSION = "0.3.4-3.0.0";
    public static final int CONFIG_VERSION = 3;
    public static final int DATABASE_VERSION = 1;

    /**
     * Destinations
     */
    private static Destinations instance;

    @Inject
    private Game game;

    @Inject
    private static Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    private DefaultConfig defaultConfig;
    private Database database;
    private boolean webServerRunning = false;
    private CallService callService;

    /**
     * @return Destinations
     */
    public static Destinations getInstance() {
        return instance;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

    /**
     * @return Logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * @return DefaultConfig
     */
    public DefaultConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    /**
     * @return Database
     */
    public Database getDatabase() {
        return this.database;
    }

    /**
     * @return CallService
     */
    public CallService getCallService() {
        return this.callService;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        Destinations.instance = this;
        Destinations.logger = LoggerFactory.getLogger(Destinations.NAME);
        getLogger().info(String.format("Starting up %s v%s.", Destinations.NAME, Destinations.VERSION));

        if (!this.configDir.isDirectory()) {
            if (this.configDir.mkdirs()) {
                getLogger().info("Destinations config directory successfully created!");
            }
        }

        // Load default config
        this.defaultConfig = new DefaultConfig(this, this.configDir);
        this.defaultConfig.load();

        // Run config migrations
        int configVersion = this.defaultConfig.get().getNode(DefaultConfig.CONFIG_VERSION).getInt(0);
        ConfigMigrationRunner configMigrationRunner = new ConfigMigrationRunner(this, configVersion);
        configMigrationRunner.run();

        // Setup database
        this.setupDatabase();

        // Run database migrations
        int databaseVersion = this.defaultConfig.get().getNode(DefaultConfig.DATABASE_VERSION).getInt(0);
        DatabaseMigrationRunner databaseMigrationRunner = new DatabaseMigrationRunner(this, databaseVersion);
        databaseMigrationRunner.run();
    }

    @Listener
    public void onInit(GameInitializationEvent event) {

        // RegisterEvents
        game.getEventManager().registerListeners(this, new BackListener(this));

        // Register back on death if enabled
        if (this.getDefaultConfig().get().getNode(DefaultConfig.BACK_SETTINGS, DefaultConfig.SAVE_ON_DEATH).getBoolean()) {
            game.getEventManager().registerListeners(this, new DeathListener(this));
        }

        /**
         * /home [name]
         */
        CommandSpec homeCommand = CommandSpec.builder()
                .description(Text.of("Teleport Home"))
                .extendedDescription(Text.of("Teleport to the nearest home or to the named home. Optional: /home [name]"))
                .executor(new HomeCommand(this))
                .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("name"))))
                .build();

        /**
         * /sethome [-f] [name]
         */
        CommandSpec setHomeCommand = CommandSpec.builder()
                .description(Text.of("Set location as home"))
                .extendedDescription(Text.of("Set this location as a home. Optional: /sethome [name]"))
                .executor(new SetHomeCommand(this))
                .arguments(GenericArguments.flags().flag("f").buildWith(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("name")))))
                .build();

        /**
         * /listhomes [page]
         */
        CommandSpec listHomesCommand = CommandSpec.builder()
                .description(Text.of("Show list of homes"))
                .extendedDescription(Text.of("Displays a list of your homes. Optional: /listhomes [page]"))
                .executor(new ListHomesCommand(this))
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("page"))))
                .build();

        /**
         * /delhome [-f] [-c] <name>
         */
        CommandSpec delHomeCommand = CommandSpec.builder()
                .description(Text.of("Delete a home"))
                .extendedDescription(Text.of("Delete a home by name. Required: /sethome <name>"))
                .executor(new DelHomeCommand(this))
                .arguments(GenericArguments.flags().flag("c").flag("f").buildWith(GenericArguments.remainingJoinedStrings(Text.of("name"))))
                .build();

        // Register home commands if enabled
        if (this.getDefaultConfig().get().getNode(DefaultConfig.HOME_SETTINGS, DefaultConfig.ENABLED).getBoolean()) {

            game.getCommandManager().register(this, homeCommand, "home", "h");
            game.getCommandManager().register(this, setHomeCommand, "sethome");
            game.getCommandManager().register(this, listHomesCommand, "listhomes", "homes");
            game.getCommandManager().register(this, delHomeCommand, "delhome");

        }

        /**
         * /warp <name>
         */
        CommandSpec warpCommand = CommandSpec.builder()
                .description(Text.of("Teleport to Warp"))
                .extendedDescription(Text.of("Teleport to the warp of the provided name."))
                .executor(new WarpCommand(this))
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("name")))
                .build();

        /**
         * /setwarp <name>
         */
        CommandSpec setWarpCommand = CommandSpec.builder()
                .description(Text.of("Set a warp"))
                .extendedDescription(Text.of("Set this location as a public warp."))
                .executor(new SetWarpCommand(this))
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("name")))
                .build();

        /**
         * /listwarps [page]
         */
        CommandSpec listWarpsCommand = CommandSpec.builder()
                .description(Text.of("Show list of warps"))
                .extendedDescription(Text.of("Displays a list of your warps. Optional: /listwarps [page]"))
                .executor(new ListWarpsCommand(this))
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("page"))))
                .build();

        /**
         * /delwarp [-f] [-c] <name>
         */
        CommandSpec delWarpCommand = CommandSpec.builder()
                .description(Text.of("Delete a warp"))
                .extendedDescription(Text.of("Delete a warp by name. Required: /delwarp <name>"))
                .executor(new DelWarpCommand(this))
                .arguments(GenericArguments.flags().flag("c").flag("f").buildWith(GenericArguments.remainingJoinedStrings(Text.of("name"))))
                .build();

        // Register warp commands if enabled
        if (this.getDefaultConfig().get().getNode(DefaultConfig.WARP_SETTINGS, DefaultConfig.ENABLED).getBoolean()) {

            game.getCommandManager().register(this, warpCommand, "warp", "w");
            game.getCommandManager().register(this, setWarpCommand, "setwarp");
            game.getCommandManager().register(this, listWarpsCommand, "listwarps", "warps");
            game.getCommandManager().register(this, delWarpCommand, "delwarp");

        }

        /**
         * /call <player>, /tpa <player>
         */
        CommandSpec callCommand = CommandSpec.builder()
                .description(Text.of("Requests a player to teleport you."))
                .extendedDescription(Text.of("Requests a player to teleport you to their current location. Required: /call <player>"))
                .executor(new CallCommand(this))
                .arguments(GenericArguments.player(Text.of("player")))
                .build();

        /**
         * /bring [player] [page]
         */
        CommandSpec bringCommand = CommandSpec.builder()
                .description(Text.of("Bring a calling player to you."))
                .extendedDescription(Text.of("Teleports a player that has issued a call request to your current location."))
                .executor(new BringCommand(this))
                .arguments(GenericArguments.optional(GenericArguments.firstParsing(GenericArguments.player(Text.of("player")), GenericArguments.integer(Text.of("page")))))
                .build();

        /**
         * /grab <player>, /tphere <player>
         */
        CommandSpec grabCommand = CommandSpec.builder()
                .description(Text.of("Teleport a player to you."))
                .extendedDescription(Text.of("Teleports a player to your current location. Required: /tphere <name>"))
                .executor(new GrabCommand(this))
                .arguments(GenericArguments.player(Text.of("player")))
                .build();

        // Register call and bring commands if enabled
        if (this.getDefaultConfig().get().getNode(DefaultConfig.TELEPORT_SETTINGS, DefaultConfig.ENABLED).getBoolean()) {

            // Load callService
            this.callService = new CallService(this);

            game.getCommandManager().register(this, callCommand, "call", "tpa");
            game.getCommandManager().register(this, bringCommand, "bring");
            game.getCommandManager().register(this, grabCommand, "grab", "tphere");

        }

        /**
         * /back
         */
        CommandSpec backCommand = CommandSpec.builder()
                .description(Text.of("Teleport back."))
                .extendedDescription(Text.of("Teleport to your previous location."))
                .executor(new BackCommand(this))
                .build();

        // Register back command if enabled
        if (this.getDefaultConfig().get().getNode(DefaultConfig.BACK_SETTINGS, DefaultConfig.ENABLED).getBoolean()) {

            game.getCommandManager().register(this, backCommand, "back", "b");

        }

        // Load Commando events
        if (game.getPluginManager().getPlugin("Commando").isPresent()) {
            game.getEventManager().registerListeners(this, new ConvertTextListener(this));
        }

    }

    @Listener
    public void onServerStart(GameAboutToStartServerEvent event) {
        this.startWebServer();
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        if (this.database instanceof H2EmbeddedDatabase && this.webServerRunning) {
            ((H2EmbeddedDatabase) this.database).stopWebServer();
            this.webServerRunning = false;
        }
    }

    private void setupDatabase() {

        this.database = new H2EmbeddedDatabase(this.getGame(), "destinations", "admin", "");
        this.startWebServer();

        TestConnectionDam testConnectionDam = new TestConnectionDam(this.database);
        if (testConnectionDam.testConnection()) {
            getLogger().info("Database connected successfully.");
        } else {
            getLogger().info("Unable to connect to database.");
        }

    }

    private void startWebServer() {
        CommentedConfigurationNode dbConfig = this.defaultConfig.get().getNode(DefaultConfig.DATABASE_SETTINGS);
        if (dbConfig.getNode(DefaultConfig.WEBSERVER).getBoolean()) {
            if (this.database instanceof H2EmbeddedDatabase && !this.webServerRunning) {
                if (((H2EmbeddedDatabase) this.database).startWebServer()) {
                    this.webServerRunning = true;
                }
            }
        }
    }

    public Destinations() {
    }
}
