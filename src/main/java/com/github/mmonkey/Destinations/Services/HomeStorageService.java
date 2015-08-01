package com.github.mmonkey.Destinations.Services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.mmonkey.Destinations.Models.HomeModel;
import org.spongepowered.api.entity.player.Player;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import com.github.mmonkey.Destinations.Models.DestinationModel;
import com.github.mmonkey.Destinations.Destinations;

public class HomeStorageService extends DestinationStorageService {

	public void saveHome(CommentedConfigurationNode config, HomeModel home) {
		
		List<String> list = getList(config);
		
		if (!list.contains(home.getName())) {
			
			list.add(home.getName());
			config.getNode(StorageService.LIST).setValue(list);
			saveDestination(config.getNode(home.getName()), home.getDestination());
		
		}
		
	}
	
	public List<String> getHomeList(Player player) {
		
		CommentedConfigurationNode config = getConfig().getNode(player.getUniqueId().toString());
		return getList(config);
		
	}
	
	public ArrayList<HomeModel> getHomes(Player player) {
		
		CommentedConfigurationNode config = getConfig().getNode(player.getUniqueId().toString());
		List<String> list = getList(config);
		ArrayList<HomeModel> homes = new ArrayList<HomeModel>();
		
		for (String name: list) {
			
			DestinationModel destination = getDestination(config.getNode(name));
			HomeModel home = new HomeModel(name, destination);
			homes.add(home);
			
		}
		
		return homes;
		
	}
	
	public void addHome(Player player, HomeModel home) {
		
		saveHome(getConfig().getNode(player.getUniqueId().toString()), home);
		saveConfig();
	
	}
	
	public boolean removeHome(Player player, String home) {
		
		CommentedConfigurationNode config = getConfig().getNode(player.getUniqueId().toString());
		List<String> list = getHomeList(player);
		
		if (list.contains(home)) {
			
			list.remove(home);
			
			config.removeChild(home);
			config.removeChild(StorageService.LIST);
			config.getNode(StorageService.LIST).setValue(list);
			
			saveConfig();
			return true;
			
		}
		
		return false;
		
	}
	
	public void updateHome(Player player, HomeModel home) {
		
		List<String> list = getHomeList(player);
		
		if (list.contains(home.getName())) {
			
			DestinationModel destination = home.getDestination();
			saveDestination(getConfig().getNode(player.getUniqueId().toString(), home.getName()), destination);
			saveConfig();
			
		}
		
	}
	
	public HomeStorageService(Destinations plugin, File configDir) {
		super(plugin, configDir);

		setConfigFile(new File(configDir, "homes.conf"));
	}

}
