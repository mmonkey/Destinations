package com.github.mmonkey.Destinations.Models;


import org.spongepowered.api.entity.living.player.Player;

public class HomeModel {

	private int id;
	private String name;
	private DestinationModel destination;

	public int getId() {
		return this.id;
	}
	
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
	
	public HomeModel(int id, String name, Player player) {
		this.id = id;
		this.name = name;
	}
	
	public HomeModel(int id, String name, DestinationModel destination) {
		this.id = id;
		this.name = name;
		this.destination = destination;
	}
	
}
