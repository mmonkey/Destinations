package com.github.mmonkey.Destinations.Services;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.github.mmonkey.Destinations.Models.WarpModel;
import org.spongepowered.api.entity.player.Player;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import com.github.mmonkey.Destinations.Destinations;

public class WarpStorageService extends DestinationStorageService {
	
	public static final String OWNER = "owner";
	public static final String IS_PUBLIC = "isPublic";
	public static final String WHITELIST = "whitelist";
	public static final String EDITORS = "editors";
	
	private void saveWhitelist(WarpModel warp) {
		CommentedConfigurationNode config = getConfig().getNode(warp.getName(), WHITELIST);
		for (Map.Entry<UUID, Boolean> item: warp.getWhitelist().entrySet()) {
			config.getNode(item.getKey().toString()).setValue(item.getValue());
		}
	}
	
	public boolean addWarp(WarpModel warp) {

		List<String> list = getList(getConfig());
		
		if(list.contains(warp.getName())) {
			return false;
		}
		
		list.add(warp.getName());
		getConfig().getNode(LIST).setValue(list);
		
		CommentedConfigurationNode config = getConfig().getNode(warp.getName());
		config.getNode(OWNER).setValue(warp.getOwnerUniqueId().toString());
		config.getNode(IS_PUBLIC).setValue(warp.isPublic());
		saveDestination(config, warp.getDestination());
		
		saveWhitelist(warp);
		saveConfig();
		
		return true;
		
	}
	
	public boolean removeWarp(WarpModel warp) {
		
		List<String> list = getList(getConfig());
		
		if (list.contains(warp.getName())) {
			
			list.remove(warp.getName());
			
			getConfig().removeChild(warp.getName());
			getConfig().removeChild(LIST);
			getConfig().getNode(LIST).setValue(list);
			
			saveConfig();
			
			return true;
			
		}
		
		return false;
		
	}
	
	private Map<UUID, Boolean> getWhitelist(CommentedConfigurationNode config) {
		
		Map<Object, ? extends CommentedConfigurationNode> mappings = config.getNode(WHITELIST).getChildrenMap();
		Map<UUID, Boolean> whitelist = new HashMap<UUID, Boolean>();
		
		for (Map.Entry<Object, ? extends CommentedConfigurationNode> item: mappings.entrySet()) {
			if (item.getKey() instanceof String) {
				whitelist.put(UUID.fromString((String) item.getKey()), item.getValue().getBoolean());
			}
		}
		
		return whitelist;
		
	}
	
	public Collection<WarpModel> getPlayerWarps(Player player) {
		
		Collection<WarpModel> warps = getWarps();
		
		for (WarpModel warp: warps) {
			
			if (!warp.isPublic()
				&& !warp.getWhitelist().containsKey(player.getUniqueId())
				&& !warp.getOwnerUniqueId().equals(player.getUniqueId())) {
				
				warps.remove(warp);
			
			}
			
		}
		
		return warps;
		
	}
	
	public WarpModel getWarp(String name) {
		
		Collection<WarpModel> warps = getWarps();
		
		for (WarpModel warp: warps) {
			if (warp.getName().equalsIgnoreCase(name)) {
				return warp;
			}
		}
		
		return null;
	}
	
	public Collection<WarpModel> getWarps() {
		
		List<String> list = getList(getConfig());
		Collection<WarpModel> warps = new ArrayList<WarpModel>();
		
		for (String item: list) {
			
			CommentedConfigurationNode config = getConfig().getNode(item);
			
			WarpModel warp = new WarpModel();
			warp.setName(item);
			warp.setOwnerUniqueId(UUID.fromString(config.getNode(OWNER).getString()));
			warp.setDestination(getDestination(config));
			warp.isPublic(config.getNode(IS_PUBLIC).getBoolean());

			Map<UUID, Boolean> whitelist = getWhitelist(config);
			warp.setWhitelist(whitelist);
			
			warps.add(warp);
			
		}
		
		return warps;
		
	}
	
	public List<String> getWarpList() {
		return getList(getConfig());
	}
	
	public List<String> getPlayerWarpList(Player player) {
		
		Collection<WarpModel> warps = getPlayerWarps(player);
		List<String> list = new ArrayList<String>();
		
		for (WarpModel warp: warps) {
			 list.add(warp.getName());
		}
		
		return list;
		
	}

	public WarpStorageService(Destinations plugin, File configDir) {
		super(plugin, configDir);
		
		setConfigFile(new File(configDir, "warps.conf"));
	}

}
