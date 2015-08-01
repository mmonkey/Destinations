package com.github.mmonkey.Destinations.Models;

import org.spongepowered.api.entity.player.Player;

public class HomeModel {
	
	private String name;
	private DestinationModel destination;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public DestinationModel getDestination() {
		return this.destination;
	}
	
	public void setDestination(DestinationModel destination) {
		this.destination = destination;
	}
	
	public HomeModel(String name, Player player) {
		this.name = name;
		this.destination = new DestinationModel(player);
	}
	
	public HomeModel(String name, DestinationModel destination) {
		this.name = name;
		this.destination = destination;
	}
	
}
