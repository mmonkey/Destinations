package com.github.mmonkey.Destinations.Models;

import java.util.Collection;
import java.util.UUID;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;

public class DestinationModel {

	private String world;
	private UUID worldUniqueId;
	private Double x;
	private Double y;
	private Double z;
	private Double yaw;
	private Double pitch;
	private Double roll;
	
	public String getWorldName() {
		return this.world;
	}
	
	public UUID getWorldUniqueId() {
		return this.worldUniqueId;
	}
	
	public World getWorld(Game game) {
		try {
			Collection<World> worlds = game.getServer().getWorlds();
			for (World world: worlds) {
				if(world.getUniqueId().equals(this.getWorldUniqueId()) && world.getName().equals(this.getWorldName())) {
					return world;
				}
			}
		} catch (Error e) {
			return null;
		}
		return null;
	}
	
	public Vector3d getRotation() {
		return new Vector3d(this.yaw, this.pitch, this.roll);
	}
	
	public Location getLocation(Game game) {
		World world = getWorld(game);
		return (world != null) ? new Location(world, this.x, this.y, this.z) : null;
	}
	
	public DestinationModel(Player player) {
		this.world = player.getWorld().getName();
		this.worldUniqueId = player.getWorld().getUniqueId();
		this.x = player.getLocation().getX();
		this.y = player.getLocation().getY();
		this.z = player.getLocation().getZ();
		this.yaw = player.getRotation().getX();
		this.pitch = player.getRotation().getY();
		this.roll = player.getRotation().getZ();
	}
	
	public DestinationModel(String world, UUID worldUniqueId, Double x, Double y, Double z, Double yaw, Double pitch, Double roll) {
		this.world = world;
		this.worldUniqueId = worldUniqueId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
	}
	
}
