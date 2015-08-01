package com.github.mmonkey.Destinations;

import java.util.Iterator;
import java.util.UUID;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;

public class Destination {

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
			Iterator<World> worlds = game.getServer().getWorlds().iterator();
			while (worlds.hasNext()) {
				World w = worlds.next();
				if(w.getUniqueId().equals(this.getWorldUniqueId()) && w.getName().equals(this.getWorldName())) {
					return w;
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
	
	public Destination(Player player) {
		this.world = player.getWorld().getName();
		this.worldUniqueId = player.getWorld().getUniqueId();
		this.x = player.getLocation().getX();
		this.y = player.getLocation().getY();
		this.z = player.getLocation().getZ();
		this.yaw = player.getRotation().getX();
		this.pitch = player.getRotation().getY();
		this.roll = player.getRotation().getZ();
	}
	
	public Destination(String world, UUID worldUniqueId, Double x, Double y, Double z, Double yaw, Double pitch, Double roll) {
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
