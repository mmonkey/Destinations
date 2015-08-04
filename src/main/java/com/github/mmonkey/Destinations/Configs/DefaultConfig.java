package com.github.mmonkey.Destinations.Configs;

import java.io.File;
import java.io.IOException;

import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import com.github.mmonkey.Destinations.Destinations;

public class DefaultConfig extends Config {
	
	public static final String CONFIG_VERSION = "version";
    public static final String DATABASE_VERSION = "databaseVersion";
	public static final String HOME_SETTINGS = "HomeSettings";
	public static final String WARP_SETTINGS = "WarpSettings";
	public static final String BACK_SETTINGS = "BackSettings";
	public static final String DATABASE_SETTINGS = "DatabaseSettings";
    public static final String TELEPORT_SETTINGS = "TeleportSettings";
    public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String WEBSERVER = "webserver";
	public static final String ENABLED = "enabled";
	public static final String MAX_HOMES = "maxHomes";
	public static final String SAVE_ON_DEATH = "saveOnDeath";
    public static final String EXPIRES_AFTER = "requestsExpireAfter";

	@Override
	public void load() {
		
		setConfigLoader(HoconConfigurationLoader.builder().setFile(getConfigFile()).build());
		
		try {
			
			if (!getConfigFile().isFile()) {
				getConfigFile().createNewFile();
				saveDefaults();
			}
			
			setConfig(getConfigLoader().load());
			
		} catch (IOException e) {
			
			e.printStackTrace();
		
		}
		
	}

	private void saveDefaults() {
		
		try {
			
			setConfig(getConfigLoader().load());
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}

		get().getNode(CONFIG_VERSION).setValue(0);
		get().getNode(DATABASE_SETTINGS, USERNAME).setValue("admin");
		get().getNode(DATABASE_SETTINGS, PASSWORD).setValue("");
		get().getNode(DATABASE_SETTINGS, WEBSERVER).setValue(false);
		get().getNode(HOME_SETTINGS, ENABLED).setValue(true);
		get().getNode(HOME_SETTINGS, MAX_HOMES).setValue(0);
		get().getNode(WARP_SETTINGS, ENABLED).setValue(true);
		get().getNode(BACK_SETTINGS, ENABLED).setValue(true);
		get().getNode(BACK_SETTINGS, SAVE_ON_DEATH).setValue(true);
		
		save();
		
	}
	
	public DefaultConfig(Destinations plugin, File configDir) {
		super(plugin, configDir);
		
		setConfigFile(new File(configDir, Destinations.NAME + ".conf"));
	}

}
