package com.github.mmonkey.Destinations;

import java.io.File;

import com.github.mmonkey.Destinations.Commands.*;
import com.github.mmonkey.Destinations.Dams.TestConnectionDam;
import com.github.mmonkey.Destinations.Database.Database;
import com.github.mmonkey.Destinations.Listeners.BackListener;
import com.github.mmonkey.Destinations.Listeners.DeathListener;
import com.github.mmonkey.Destinations.Migrations.*;
import com.github.mmonkey.Destinations.Migrations.ConfigMigrations.M02_AddCallBringSettings;
import com.github.mmonkey.Destinations.Migrations.ConfigMigrations.M01_AddDatabaseSettings;
import com.github.mmonkey.Destinations.Migrations.ConfigMigrations.M03_RemoveDatabaseUserCredentials;
import com.github.mmonkey.Destinations.Migrations.DatabaseMigrations.M01_AddInitialDatabaseTables;
import com.github.mmonkey.Destinations.Services.CallService;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerAboutToStartEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandSpec;

import com.github.mmonkey.Destinations.Listeners.ConvertTextListener;
import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Database.H2EmbeddedDatabase;
import com.google.common.base.Optional;
import com.google.inject.Inject;

@Plugin(id = Destinations.ID, name = Destinations.NAME, version = Destinations.VERSION)
public class Destinations {
	
	public static final String NAME = "Destinations";
	public static final String ID = "Destinations";
	public static final String VERSION = "0.3.3";
    public static final int CONFIG_VERSION = 4;
    public static final int DATABASE_VERSION = 1;
	
	private Game game;
	private Optional<PluginContainer> pluginContainer;
	private static Logger logger;
	private DefaultConfig defaultConfig;
	private Database database;
    private boolean webServerRunning = false;
    private CallService callService;
	
	@Inject
	@ConfigDir(sharedRoot = false)
	private File configDir;
	
	public Game getGame() {
		return this.game;
	}
	
	public Optional<PluginContainer> getPluginContainer() {
		return this.pluginContainer;
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	public DefaultConfig getDefaultConfig() {
		return this.defaultConfig;
	}

	public Database getDatabase() {
		return this.database;
	}

    public CallService getCallService() {
        return this.callService;
    }
	
	@Subscribe
	public void onPreInit(PreInitializationEvent event) {
		
		this.game = event.getGame();
		this.pluginContainer = game.getPluginManager().getPlugin(Destinations.NAME);
		Destinations.logger = game.getPluginManager().getLogger(pluginContainer.get());
		
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
	
	@Subscribe
	public void onInit(InitializationEvent event) {

        // RegisterEvents
        game.getEventManager().register(this, new BackListener(this));

        // Register back on death if enabled
        if (this.getDefaultConfig().get().getNode(DefaultConfig.BACK_SETTINGS, DefaultConfig.SAVE_ON_DEATH).getBoolean()) {
            game.getEventManager().register(this, new DeathListener(this));
        }

		
		/**
		 * /home [name]
		 */
		CommandSpec homeCommand = CommandSpec.builder()
			.description(Texts.of("Teleport Home"))
			.extendedDescription(Texts.of("Teleport to the nearest home or to the named home. Optional: /home [name]"))
			.executor(new HomeCommand(this))
			.arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("name"))))
			.build();
		
		/**
		 * /sethome [-f] [name]
		 */
		CommandSpec setHomeCommand = CommandSpec.builder()
			.description(Texts.of("Set location as home"))
			.extendedDescription(Texts.of("Set this location as a home. Optional: /sethome [name]"))
			.executor(new SetHomeCommand(this))
			.arguments(GenericArguments.flags().flag("f").buildWith(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("name")))))
			.build();
		
		/**
		 * /listhomes [page]
		 */
		CommandSpec listHomesCommand = CommandSpec.builder()
			.description(Texts.of("Show list of homes"))
			.extendedDescription(Texts.of("Displays a list of your homes. Optional: /listhomes [page]"))
			.executor(new ListHomesCommand(this))
			.arguments(GenericArguments.optional(GenericArguments.integer(Texts.of("page"))))
			.build();
		
		/**
		 * /delhome [-f] [-c] <name>
		 */
		CommandSpec delHomeCommand = CommandSpec.builder()
			.description(Texts.of("Delete a home"))
			.extendedDescription(Texts.of("Delete a home by name. Required: /sethome <name>"))
			.executor(new DelHomeCommand(this))
			.arguments(GenericArguments.flags().flag("c").flag("f").buildWith(GenericArguments.remainingJoinedStrings(Texts.of("name"))))
			.build();
		
		// Register home commands if enabled
		if (this.getDefaultConfig().get().getNode(DefaultConfig.HOME_SETTINGS, DefaultConfig.ENABLED).getBoolean()) {
			
			game.getCommandDispatcher().register(this, homeCommand, "home", "h");
			game.getCommandDispatcher().register(this, setHomeCommand, "sethome");
			game.getCommandDispatcher().register(this, listHomesCommand, "listhomes", "homes");
			game.getCommandDispatcher().register(this, delHomeCommand, "delhome");
		
		}
		
		/**
		 * /warp <name>
		 */
		CommandSpec warpCommand = CommandSpec.builder()
			.description(Texts.of("Teleport to Warp"))
			.extendedDescription(Texts.of("Teleport to the warp of the provided name."))
			.executor(new WarpCommand(this))
			.arguments(GenericArguments.remainingJoinedStrings(Texts.of("name")))
			.build();
		
		/**
		 * /setwarp <name>
		 */
		CommandSpec setWarpCommand = CommandSpec.builder()
			.description(Texts.of("Set a warp"))
			.extendedDescription(Texts.of("Set this location as a public warp."))
			.executor(new SetWarpCommand(this))
			.arguments(GenericArguments.remainingJoinedStrings(Texts.of("name")))
			.build();
		
		/**
		 * /listwarps [page]
		 */
		CommandSpec listWarpsCommand = CommandSpec.builder()
			.description(Texts.of("Show list of warps"))
			.extendedDescription(Texts.of("Displays a list of your warps. Optional: /listwarps [page]"))
			.executor(new ListWarpsCommand(this))
			.arguments(GenericArguments.optional(GenericArguments.integer(Texts.of("page"))))
			.build();
		
		/**
		 * /delwarp [-f] [-c] <name>
		 */
		CommandSpec delWarpCommand = CommandSpec.builder()
			.description(Texts.of("Delete a warp"))
			.extendedDescription(Texts.of("Delete a warp by name. Required: /delwarp <name>"))
			.executor(new DelWarpCommand(this))
			.arguments(GenericArguments.flags().flag("c").flag("f").buildWith(GenericArguments.remainingJoinedStrings(Texts.of("name"))))
			.build();
		
		// Register warp commands if enabled
		if (this.getDefaultConfig().get().getNode(DefaultConfig.WARP_SETTINGS, DefaultConfig.ENABLED).getBoolean()) {
		
			game.getCommandDispatcher().register(this, warpCommand, "warp", "w");
			game.getCommandDispatcher().register(this, setWarpCommand, "setwarp");
			game.getCommandDispatcher().register(this, listWarpsCommand, "listwarps", "warps");
			game.getCommandDispatcher().register(this, delWarpCommand, "delwarp");
			
		}

		/**
		 * /call <player>, /tpa <player>
		 */
		CommandSpec callCommand = CommandSpec.builder()
				.description(Texts.of("Requests a player to teleport you."))
				.extendedDescription(Texts.of("Requests a player to teleport you to their current location. Required: /call <player>"))
				.executor(new CallCommand(this))
				.arguments(GenericArguments.player(Texts.of("player"), game))
				.build();

		/**
		 * /bring [player] [page]
		 */
		CommandSpec bringCommand = CommandSpec.builder()
				.description(Texts.of("Bring a calling player to you."))
				.extendedDescription(Texts.of("Teleports a player that has issued a call request to your current location."))
				.executor(new BringCommand(this))
				.arguments(GenericArguments.optional(GenericArguments.firstParsing(GenericArguments.player(Texts.of("player"), game), GenericArguments.integer(Texts.of("page")))))
				.build();

        /**
         * /grab <player>, /tphere <player>
         */
        CommandSpec grabCommand = CommandSpec.builder()
                .description(Texts.of("Teleport a player to you."))
                .extendedDescription(Texts.of("Teleports a player to your current location. Required: /tphere <name>"))
                .executor(new GrabCommand(this))
                .arguments(GenericArguments.player(Texts.of("player"), game))
                .build();

        // Register call and bring commands if enabled
        if (this.getDefaultConfig().get().getNode(DefaultConfig.TELEPORT_SETTINGS, DefaultConfig.ENABLED).getBoolean()) {

            // Load callService
            this.callService = new CallService(this, event.getGame().getScheduler());

            game.getCommandDispatcher().register(this, callCommand, "call", "tpa");
            game.getCommandDispatcher().register(this, bringCommand, "bring");
            game.getCommandDispatcher().register(this, grabCommand, "grab", "tphere");

        }

		/**
		 * /back
		 */
		CommandSpec backCommand = CommandSpec.builder()
				.description(Texts.of("Teleport back."))
				.extendedDescription(Texts.of("Teleport to your previous location."))
				.executor(new BackCommand(this))
                .build();

        // Register back command if enabled
        if (this.getDefaultConfig().get().getNode(DefaultConfig.BACK_SETTINGS, DefaultConfig.ENABLED).getBoolean()) {

            game.getCommandDispatcher().register(this, backCommand, "back", "b");

        }

        // Load Commando events
        if (game.getPluginManager().getPlugin("Commando").isPresent()) {
            game.getEventManager().register(this, new ConvertTextListener(this));
        }

	}

    @Subscribe
    public void onServerStart(ServerAboutToStartEvent event) {
        this.startWebServer();
    }
	
	@Subscribe
	public void onServerStop(ServerStoppingEvent event) {
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
