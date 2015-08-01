package com.github.mmonkey.Destinations.Services;

import java.io.File;
import java.util.UUID;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import org.spongepowered.api.world.Location;

import com.flowpowered.math.vector.Vector3d;
import com.github.mmonkey.Destinations.Models.DestinationModel;
import com.github.mmonkey.Destinations.Destinations;

public class DestinationStorageService extends StorageService {

    public static final String DESTINATION = "destination";
    public static final String WORLD_UUID = "worldUUID";
    public static final String LOCATION_X = "locationX";
    public static final String LOCATION_Y = "locationY";
    public static final String LOCATION_Z = "locationZ";
    public static final String ROTATION_X = "rotationX";
    public static final String ROTATION_Y = "rotationY";
    public static final String ROTATION_Z = "rotationZ";

    public void saveDestination(CommentedConfigurationNode config, DestinationModel destination) {

        UUID worldUniqueId = destination.getWorldUniqueId();
        Location location = destination.getLocation(getPlugin().getGame());
        Vector3d rotation = destination.getRotation();

        config.getNode(DESTINATION, WORLD_UUID).setValue(worldUniqueId.toString());
        config.getNode(DESTINATION, LOCATION_X).setValue(location.getX());
        config.getNode(DESTINATION, LOCATION_Y).setValue(location.getY());
        config.getNode(DESTINATION, LOCATION_Z).setValue(location.getZ());
        config.getNode(DESTINATION, ROTATION_X).setValue(rotation.getX());
        config.getNode(DESTINATION, ROTATION_Y).setValue(rotation.getY());
        config.getNode(DESTINATION, ROTATION_Z).setValue(rotation.getZ());

    }

    public DestinationModel getDestination(CommentedConfigurationNode config) {

        CommentedConfigurationNode destinationConfig = config.getNode(DESTINATION);

        UUID worldUniqueId = (destinationConfig.getNode(WORLD_UUID).getString() == null) ? null : UUID.fromString(destinationConfig.getNode(WORLD_UUID).getString());

        return new DestinationModel(
                worldUniqueId,
                destinationConfig.getNode(LOCATION_X).getDouble(),
                destinationConfig.getNode(LOCATION_Y).getDouble(),
                destinationConfig.getNode(LOCATION_Z).getDouble(),
                destinationConfig.getNode(ROTATION_X).getDouble(),
                destinationConfig.getNode(ROTATION_Y).getDouble(),
                destinationConfig.getNode(ROTATION_Z).getDouble()
        );
    }

    public DestinationStorageService(Destinations plugin, File configDir) {
        super(plugin, configDir);
    }

}
