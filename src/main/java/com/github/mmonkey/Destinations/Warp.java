package com.github.mmonkey.Destinations;

import java.util.ArrayList;
import java.util.UUID;

import org.spongepowered.api.entity.player.Player;

import com.github.mmonkey.Destinations.Utilities.DestinationTypes;

public class Warp {

	private String name;
	private UUID ownerUniqueId;
	private Destination destination;
	private boolean isPublic;
	private ArrayList<UUID> whiteList;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public UUID getOwnerUniqueId() {
		return this.ownerUniqueId;
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
	
	public ArrayList<UUID> getWhiteList() {
		return this.whiteList;
	}
	
	public Warp(String name, Player player) {
		this.name = name;
		this.ownerUniqueId = player.getUniqueId();
		this.destination = new Destination(player, DestinationTypes.WARP);
		this.isPublic = true;
		this.whiteList = new ArrayList<UUID>();
	}
	
}
