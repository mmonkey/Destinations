package com.github.mmonkey.Destinations.Utilities;

import java.util.HashMap;

import org.spongepowered.api.entity.player.Player;

import com.github.mmonkey.Destinations.Home;

public class TemporaryStorageUtil {

	private HashMap<Player, Home> tempHomeStorage = new HashMap<Player, Home>();
	
	public HashMap<Player, Home> getTempHomeStorage() {
		return this.tempHomeStorage;
	}
	
	public TemporaryStorageUtil(){
	}
	
}
