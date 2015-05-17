package com.github.mmonkey.Destinations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.spongepowered.api.entity.player.Player;

import com.github.mmonkey.Destinations.Utilities.DestinationTypes;

public class Warp {

	private String name;
	private UUID ownerUniqueId;
	private Destination destination;
	private boolean isPublic;
	private Collection<UUID> editors;
	private Collection<UUID> whitelist;
	
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
	
	public Collection<UUID> getWhitelist() {
		return this.whitelist;
	}
	
	public void setWhitelist(Collection<UUID> whitelist) {
		this.whitelist = whitelist;
	}
	
	public boolean addToWhitelist(UUID playerUniqueId) {
		return this.whitelist.add(playerUniqueId);
	}
	
	public boolean addToWhitelist(Collection<UUID> playerUniqueIds) {
		return this.whitelist.addAll(playerUniqueIds);
	}
	
	public Collection<UUID> getEditors() {
		return this.editors;
	}
	
	public void setEditors(Collection<UUID> editors) {
		this.editors = editors;
	}
	
	public boolean addEditor(UUID playerUniqueId) {
		return this.editors.add(playerUniqueId);
	}
	
	public boolean addEditors(Collection<UUID> playerUniqueIds) {
		return this.editors.addAll(playerUniqueIds);
	}
	
	public Warp() {
		this.isPublic = true;
		this.whitelist = new ArrayList<UUID>();
		this.editors = new ArrayList<UUID>();
	}
	
	public Warp(String name, Player player) {
		this.name = name;
		this.ownerUniqueId = player.getUniqueId();
		this.destination = new Destination(player, DestinationTypes.WARP);
		this.isPublic = true;
		this.whitelist = new ArrayList<UUID>();
		this.editors = new ArrayList<UUID>();
	}
	
}
