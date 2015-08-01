package com.github.mmonkey.Destinations.Services;

import java.io.File;
import java.io.IOException;

import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import com.github.mmonkey.Destinations.Destinations;

public class DefaultConfigStorageService extends StorageService {
	
	public static final String VERSION = "version";
	public static final String HOME_SETTINGS = "HomeSettings";
	public static final String WARP_SETTINGS = "WarpSettings";
	public static final String BACK_SETTINGS = "BackSettings";
	public static final String DATABASE_SETTINGS = "DatabaseSettings";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String WEBSERVER = "webserver";
	public static final String ENABLED = "enabled";
	public static final String MAX_HOMES = "maxHomes";
	public static final String SAVE_ON_DEATH = "saveOnDeath";

	@Override
	public void load() {
		
		setConfigLoader(HoconConfigurationLoader.builder().setFile(getConfigFile()).build());
		
		try {
			
			if (!getConfigFile().isFile()) {
				getConfigFile().createNewFile();
				save();
			}
			
			setConfig(getConfigLoader().load());
			
		} catch (IOException e) {
			
			e.printStackTrace();
		
		}
		
	}

	public void save() {
		
		try {
			
			setConfig(getConfigLoader().load());
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}

		getConfig().getNode(VERSION).setValue(0);
		getConfig().getNode(DATABASE_SETTINGS, USERNAME).setValue("admin");
		getConfig().getNode(DATABASE_SETTINGS, PASSWORD).setValue("");
		getConfig().getNode(DATABASE_SETTINGS, WEBSERVER).setValue(false);
		getConfig().getNode(HOME_SETTINGS, ENABLED).setValue(true);
		getConfig().getNode(HOME_SETTINGS, MAX_HOMES).setValue(0);
		getConfig().getNode(WARP_SETTINGS, ENABLED).setValue(true);
		getConfig().getNode(BACK_SETTINGS, ENABLED).setValue(true);
		getConfig().getNode(BACK_SETTINGS, SAVE_ON_DEATH).setValue(true);
		
		saveConfig();
		
	}
	
	public DefaultConfigStorageService(Destinations plugin, File configDir) {
		super(plugin, configDir);
		
		setConfigFile(new File(configDir, Destinations.NAME + ".conf"));
	}

}
