package com.github.mmonkey.Destinations;

import org.spongepowered.api.entity.player.Player;
import com.github.mmonkey.Destinations.Utilities.DestinationTypes;

public class Home {
	
	private String name;
	private Destination destination;
	
	public Home(String name, Player player) {
		this.name = name;
		this.destination = new Destination(player, DestinationTypes.HOME);
	}
	
	public Home(String name, Destination destination) {
		this.name = name;
		this.destination = destination;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Destination getDestination() {
		return this.destination;
	}
	
	public void setDestination(Destination destination) {
		this.destination = destination;
	}
}
