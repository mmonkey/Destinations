package com.github.mmonkey.Destinations.Services;

import java.io.File;
import java.io.IOException;

import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Utilities.StorageUtil;

public class DefaultConfigStorageService extends StorageService {

	public DefaultConfigStorageService(Destinations plugin, File configDir) {
		super(plugin, configDir);
		
		setConfigFile(new File(configDir, Destinations.NAME + ".conf"));
	}

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
		
		getConfig().getNode(StorageUtil.CONFIG_NODE_VERSION).setValue(StorageUtil.DEFAULT_CONFIG_VERSION);
		getConfig().getNode(StorageUtil.CONFIG_NODE_HOME_SETTINGS, StorageUtil.CONFIG_NODE_ENABLED).setValue(true);
		getConfig().getNode(StorageUtil.CONFIG_NODE_HOME_SETTINGS, StorageUtil.CONFIG_NODE_MAX_HOMES).setValue(0);
		getConfig().getNode(StorageUtil.CONFIG_NODE_WARP_SETTINGS, StorageUtil.CONFIG_NODE_ENABLED).setValue(true);
		getConfig().getNode(StorageUtil.CONFIG_NODE_BACK_SETTINGS, StorageUtil.CONFIG_NODE_ENABLED).setValue(true);
		getConfig().getNode(StorageUtil.CONFIG_NODE_BACK_SETTINGS, StorageUtil.CONFIG_NODE_SAVE_ON_DEATH).setValue(true);
		
		saveConfig();
		
		Destinations.getLogger().info(String.format("Created default configuration v%d.", StorageUtil.DEFAULT_CONFIG_VERSION));
		
	}

}
