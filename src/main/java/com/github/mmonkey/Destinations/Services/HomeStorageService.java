package com.github.mmonkey.Destinations.Services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.entity.player.Player;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import com.github.mmonkey.Destinations.Destination;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Home;
import com.github.mmonkey.Destinations.Utilities.StorageUtil;

public class HomeStorageService extends DestinationStorageService {

	public HomeStorageService(Destinations plugin, File configDir) {
		super(plugin, configDir);

		setConfigFile(new File(configDir, "homes.conf"));
	}


	public void saveHome(CommentedConfigurationNode config, Home home) {
		
		String name = home.getName();
		Destination destination = home.getDestination();
		List<String> list = getList(config);
		list.add(name);
		
		// Add list item
		config.getNode(StorageUtil.CONFIG_NODE_LIST).setValue(list);
		
		// Add destination
		saveDestination(config.getNode(name), destination);
		
	}
	
	public List<String> getHomeList(Player player) {
		
		CommentedConfigurationNode config = getConfig().getNode(player.getUniqueId().toString());
		return getList(config);
		
	}
	
	public ArrayList<Home> getHomes(Player player) {
		
		CommentedConfigurationNode config = getConfig().getNode(player.getUniqueId().toString());
		List<String> list = getList(config);
		ArrayList<Home> homes = new ArrayList<Home>();
		
		for (String name: list) {
			Destination destination = getDestination(config.getNode(name));
			Home home = new Home(name, destination);
			homes.add(home);
		}
		
		return homes;
		
	}
	
	public void addHome(Player player, Home home) {
		
		saveHome(getConfig().getNode(player.getUniqueId().toString()), home);
		saveConfig();
	
	}
	
	public boolean removeHome(Player player, String home) {
		
		CommentedConfigurationNode config = getConfig().getNode(player.getUniqueId().toString());
		List<String> list = getHomeList(player);
		
		if (list.contains(home)) {
			
			list.remove(home);
			
			config.removeChild(home);
			config.removeChild(StorageUtil.CONFIG_NODE_LIST);
			config.getNode(StorageUtil.CONFIG_NODE_LIST).setValue(list);
			
			saveConfig();
			return true;
			
		}
		
		return false;
		
	}
	
	public void updateHome(Player player, Home home) {
		
		List<String> list = getHomeList(player);
		
		if (list.contains(home.getName())) {
			
			Destination destination = home.getDestination();
			saveDestination(getConfig().getNode(player.getUniqueId().toString(), home.getName()), destination);
			saveConfig();
			
		}
		
	}

}
