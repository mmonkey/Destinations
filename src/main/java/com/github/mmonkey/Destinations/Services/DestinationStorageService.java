package com.github.mmonkey.Destinations.Services;

import java.io.File;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import org.spongepowered.api.world.Location;

import com.flowpowered.math.vector.Vector3d;
import com.github.mmonkey.Destinations.Destination;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Utilities.DestinationTypes;
import com.github.mmonkey.Destinations.Utilities.StorageUtil;

public class DestinationStorageService extends StorageService {

	public DestinationStorageService(Destinations plugin, File configDir) {
		super(plugin, configDir);
	}
	
	public void saveDestination(CommentedConfigurationNode config, Destination destination) {
		
		String type = destination.getType().name();
		String world = destination.getWorldName();
		Location location = destination.getLocation(getPlugin().getGame());
		Vector3d rotation = destination.getRotation();
		
		config.getNode(StorageUtil.CONFIG_NODE_DESTINATION, StorageUtil.CONFIG_NODE_DESTINATION_TYPE).setValue(type);
		config.getNode(StorageUtil.CONFIG_NODE_DESTINATION, StorageUtil.CONFIG_NODE_WORLD).setValue(world);
		config.getNode(StorageUtil.CONFIG_NODE_DESTINATION, StorageUtil.CONFIG_NODE_LOCATION_X).setValue(location.getX());
		config.getNode(StorageUtil.CONFIG_NODE_DESTINATION, StorageUtil.CONFIG_NODE_LOCATION_Y).setValue(location.getY());
		config.getNode(StorageUtil.CONFIG_NODE_DESTINATION, StorageUtil.CONFIG_NODE_LOCATION_Z).setValue(location.getZ());
		config.getNode(StorageUtil.CONFIG_NODE_DESTINATION, StorageUtil.CONFIG_NODE_ROTATION_X).setValue(rotation.getX());
		config.getNode(StorageUtil.CONFIG_NODE_DESTINATION, StorageUtil.CONFIG_NODE_ROTATION_Y).setValue(rotation.getY());
		config.getNode(StorageUtil.CONFIG_NODE_DESTINATION, StorageUtil.CONFIG_NODE_ROTATION_Z).setValue(rotation.getZ());
		
	}
	
	public Destination getDestination(CommentedConfigurationNode config) {
		
		CommentedConfigurationNode destinationConfig = (CommentedConfigurationNode) config.getNode(StorageUtil.CONFIG_NODE_DESTINATION);
		
		return new Destination(
			(String) destinationConfig.getNode(StorageUtil.CONFIG_NODE_WORLD).getString(),
			(Double) destinationConfig.getNode(StorageUtil.CONFIG_NODE_LOCATION_X).getDouble(),
			(Double) destinationConfig.getNode(StorageUtil.CONFIG_NODE_LOCATION_Y).getDouble(),
			(Double) destinationConfig.getNode(StorageUtil.CONFIG_NODE_LOCATION_Z).getDouble(),
			(Double) destinationConfig.getNode(StorageUtil.CONFIG_NODE_ROTATION_X).getDouble(),
			(Double) destinationConfig.getNode(StorageUtil.CONFIG_NODE_ROTATION_Y).getDouble(),
			(Double) destinationConfig.getNode(StorageUtil.CONFIG_NODE_ROTATION_Z).getDouble(),
			(DestinationTypes) DestinationTypes.valueOf((String) destinationConfig.getNode(StorageUtil.CONFIG_NODE_DESTINATION_TYPE).getString())
		);
	}

}
