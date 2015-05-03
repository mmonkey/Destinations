package com.github.mmonkey.Destinations;

import java.io.File;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandSpec;

import com.github.mmonkey.Destinations.Commands.DelHomeCommand;
import com.github.mmonkey.Destinations.Commands.HomeCommand;
import com.github.mmonkey.Destinations.Commands.ListHomesCommand;
import com.github.mmonkey.Destinations.Commands.SetHomeCommand;
import com.github.mmonkey.Destinations.Services.DefaultConfigStorageService;
import com.github.mmonkey.Destinations.Services.HomeStorageService;
import com.github.mmonkey.Destinations.Utilities.StorageUtil;
import com.google.common.base.Optional;
import com.google.inject.Inject;

@Plugin(id = Destinations.ID, name = Destinations.NAME, version = Destinations.VERSION)
public class Destinations {
	
	public static final String NAME = "Destinations";
	public static final String ID = "Destinations";
	public static final String VERSION = "0.0.2";
	
	private Game game;
	private Optional<PluginContainer> pluginContainer;
	private static Logger logger;
	
	private DefaultConfigStorageService defaultConfigService;
	private HomeStorageService homeStorageService;
	
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
	
	public DefaultConfigStorageService getDefaultConfigService() {
		return this.defaultConfigService;
	}
	
	public HomeStorageService getHomeStorageService() {
		return this.homeStorageService;
	}
	
	@Subscribe
	public void onPreInit(PreInitializationEvent event) {
		
		this.game = event.getGame();
		this.pluginContainer = game.getPluginManager().getPlugin(Destinations.NAME);
		Destinations.logger = game.getPluginManager().getLogger(pluginContainer.get());
		
		getLogger().info(String.format("Starting up %s v%s.", Destinations.NAME, Destinations.VERSION));
			
		if (!this.configDir.isDirectory()) {
			this.configDir.mkdirs();
		}
		
		this.defaultConfigService = new DefaultConfigStorageService(this, this.configDir);
		this.homeStorageService = new HomeStorageService(this, this.configDir);
		
		this.defaultConfigService.load();
		this.homeStorageService.load();
			
	}
	
	@Subscribe
	public void onInit(InitializationEvent event) {
		
		/**
		 * /home [name]
		 */
		CommandSpec homeCommand = CommandSpec.builder()
			.setDescription(Texts.of("Teleport Home"))
			.setExtendedDescription(Texts.of("Teleport to the nearest home or to the named home. Optional: /home [name]"))
			.setExecutor(new HomeCommand(this))
			.setArguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("name"))))
			.build();
		
		/**
		 * /sethome [-f] [name]
		 */
		CommandSpec setHomeCommand = CommandSpec.builder()
			.setDescription(Texts.of("Set location as home"))
			.setExtendedDescription(Texts.of("Set this location as a home. Optional: /sethome [name]"))
			.setExecutor(new SetHomeCommand(this))
			.setArguments(GenericArguments.flags().flag("f").buildWith(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("name")))))
			.build();
		
		/**
		 * /listhomes [page]
		 */
		CommandSpec listHomesCommand = CommandSpec.builder()
			.setDescription(Texts.of("Show list of homes"))
			.setExtendedDescription(Texts.of("Displays a list of your homes. Optional: /listhomes [page]"))
			.setExecutor(new ListHomesCommand(this))
			.setArguments(GenericArguments.optional(GenericArguments.integer(Texts.of("page"))))
			.build();
		
		/**
		 * /delhome [-f] [-c] <name>
		 */
		CommandSpec delHomeCommand = CommandSpec.builder()
			.setDescription(Texts.of("Delete a home"))
			.setExtendedDescription(Texts.of("Delete a home by name. Required: /sethome <name>"))
			.setExecutor(new DelHomeCommand(this))
			.setArguments(GenericArguments.flags().flag("c").flag("f").buildWith(GenericArguments.remainingJoinedStrings(Texts.of("name"))))
			.build();
		
		// Register home commands if enabled
		if (this.getDefaultConfigService().getConfig().getNode(StorageUtil.CONFIG_NODE_HOME_SETTINGS, StorageUtil.CONFIG_NODE_ENABLED).getBoolean()) {
			
			game.getCommandDispatcher().register(this, homeCommand, "home");
			game.getCommandDispatcher().register(this, setHomeCommand, "sethome");
			game.getCommandDispatcher().register(this, listHomesCommand, "listhomes", "homes");
			game.getCommandDispatcher().register(this, delHomeCommand, "delhome");
		
		}
			
	}
	
	public Destinations() {
	}
}
