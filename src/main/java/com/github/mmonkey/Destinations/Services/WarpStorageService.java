package com.github.mmonkey.Destinations.Services;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.entity.player.Player;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Warp;

public class WarpStorageService extends DestinationStorageService {
	
	public static final String OWNER = "owner";
	public static final String IS_PUBLIC = "isPublic";
	public static final String WHITELIST = "whitelist";
	public static final String EDITORS = "editors";
	
	public boolean addWarp(Warp warp) {

		List<String> list = getList(getConfig());
		
		if(list.contains(warp.getName())) {
			return false;
		}
		
		list.add(warp.getName());
		
		CommentedConfigurationNode config = getConfig().getNode(warp.getName());
		config.getNode(OWNER).setValue(warp.getOwnerUniqueId().toString());
		config.getNode(IS_PUBLIC).setValue(warp.isPublic());
		config.getNode(WHITELIST).setValue(warp.getWhitelist());
		config.getNode(EDITORS).setValue(warp.getEditors());
		saveDestination(config, warp.getDestination());
		
		saveConfig();
		
		return true;
		
	}
	
	public boolean removeWarp(Warp warp) {
		
		List<String> list = getList(getConfig());
		
		if (list.contains(warp.getName())) {
			
			list.remove(warp.getName());
			
			CommentedConfigurationNode config = getConfig().getNode(warp.getName());
			config.removeChild(warp.getName());
			config.removeChild(StorageService.LIST);
			config.getNode(StorageService.LIST).setValue(list);
			
			saveConfig();
			
			return true;
			
		}
		
		return false;
		
	}
	
	public Collection<Warp> getPlayerWarps(Player player) {
		
		Collection<Warp> warps = getWarps();
		
		for (Warp warp: warps) {
			
			if (!warp.isPublic()
				&& !warp.getWhitelist().contains(player.getUniqueId())
				&& !warp.getOwnerUniqueId().equals(player.getUniqueId())
				&& !warp.getEditors().contains(player.getUniqueId())) {
				
				warps.remove(warp);
			
			}
			
		}
		
		return warps;
		
	}
	
	public Collection<Warp> getWarps() {
		
		List<String> list = getList(getConfig());
		Collection<Warp> warps = new ArrayList<Warp>();
		
		for (String item: list) {
			
			CommentedConfigurationNode config = getConfig().getNode(item);
			
			Warp warp = new Warp();
			warp.setName(item);
			warp.setOwnerUniqueId(UUID.fromString(config.getNode(OWNER).getString()));
			warp.setDestination(getDestination(config));
			warp.isPublic(config.getNode(IS_PUBLIC).getBoolean());
			
			@SuppressWarnings("unchecked")
			List<String> whitelist = (List<String>) config.getNode(WHITELIST).getValue();
			
			for (String id: whitelist) {
				warp.addToWhitelist(UUID.fromString(id));
			}
			
			@SuppressWarnings("unchecked")
			List<String> editors = (List<String>) config.getNode(EDITORS).getValue();
			
			for (String id: editors) {
				warp.addEditor(UUID.fromString(id));
			}
			
		}
		
		return warps;
		
	}
	
	public List<String> getWarpList() {
		return getList(getConfig());
	}
	
	public List<String> getPlayerWarpList(Player player) {
		
		Collection<Warp> warps = getPlayerWarps(player);
		List<String> list = new ArrayList<String>();
		
		for (Warp warp: warps) {
			 list.add(warp.getName());
		}
		
		return list;
		
	}

	public WarpStorageService(Destinations plugin, File configDir) {
		super(plugin, configDir);
		
		setConfigFile(new File(configDir, "warps.conf"));
	}

}
