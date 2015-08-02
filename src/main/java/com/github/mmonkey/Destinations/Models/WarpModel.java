package com.github.mmonkey.Destinations.Models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.api.entity.player.Player;

public class WarpModel {

    private int id;
	private String name;
	private UUID ownerUniqueId;
	private DestinationModel destination;
	private boolean isPublic = true;
	private Map<UUID, Boolean> whitelist = new HashMap<UUID, Boolean>();

    public int getId() {
        return this.id;
    }
	
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
	
	public DestinationModel getDestination() {
		return this.destination;
	}
	
	public void setDestination(DestinationModel destination) {
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
	
//	public WarpModel() {
//	}
	
	public WarpModel(int id, String name, UUID playerUniqueId, DestinationModel destination, boolean isPublic) {
        this.id = id;
		this.name = name;
		this.ownerUniqueId = playerUniqueId;
		this.destination = destination;
		this.isPublic = isPublic;
	}
	
}
