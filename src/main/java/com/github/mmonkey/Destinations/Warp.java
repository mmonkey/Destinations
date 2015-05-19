package com.github.mmonkey.Destinations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.api.entity.player.Player;

import com.github.mmonkey.Destinations.Utilities.DestinationTypes;

public class Warp {

	private String name;
	private UUID ownerUniqueId;
	private Destination destination;
	private boolean isPublic = true;
	private Map<UUID, Boolean> whitelist = new HashMap<UUID, Boolean>();
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public UUID getOwnerUniqueId() {
		return this.ownerUniqueId;
	}
	
	public void setOwnerUniqueId(UUID uniqueId) {
		this.ownerUniqueId = uniqueId;
	}
	
	public Destination getDestination() {
		return this.destination;
	}
	
	public void setDestination(Destination destination) {
		this.destination = destination;
	}
	
	public boolean isPublic() {
		return this.isPublic;
	}
	
	public void isPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	
	public Map<UUID, Boolean> getWhitelist() {
		return this.whitelist;
	}
	
	public void setWhitelist(Map<UUID, Boolean> whitelist) {
		this.whitelist = whitelist;
	}
	
	public Warp() {
	}
	
	public Warp(String name, Player player) {
		this.name = name;
		this.ownerUniqueId = player.getUniqueId();
		this.destination = new Destination(player, DestinationTypes.WARP);
	}
	
}
