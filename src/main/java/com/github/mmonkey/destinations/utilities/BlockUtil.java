package com.github.mmonkey.destinations.utilities;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.trait.BooleanTraits;
import org.spongepowered.api.data.property.block.SolidCubeProperty;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class BlockUtil {

    /**
     * Is this a solid block?
     *
     * @param blockLoc Block
     * @return Whether or not this block is solid
     */
    public static boolean isSolid(Location blockLoc) {
        if (blockLoc.getProperty(SolidCubeProperty.class).isPresent()) {
            SolidCubeProperty property = (SolidCubeProperty) blockLoc.getProperty(SolidCubeProperty.class).get();
            return property.getValue();
        }
        return false;
    }

    /**
     * Is this block a bed?
     *
     * @param blockLoc Block
     * @return Whether or not this block is a bed
     */
    public static boolean isBed(Location<World> blockLoc) {
        return blockLoc.getBlock().getType().equals(BlockTypes.BED);
    }

    /**
     * Is this an occupied bed?
     *
     * @param blockLoc Block
     * @return Whether or not this block is an occupied bed
     */
    public static boolean isBedOccupied(Location<World> blockLoc) {
        return isBed(blockLoc) ? blockLoc.getBlock().getTraitValue(BooleanTraits.BED_OCCUPIED).orElse(false) : false;
    }

    /**
     * Get the distance between two locations
     *
     * @param a Location
     * @param b Location
     * @return double
     */
    public static double distance(Location a, Location b) {
        double x = Math.pow((a.getX() - b.getX()), 2);
        double y = Math.pow((a.getY() - b.getY()), 2);
        double z = Math.pow((a.getZ() - b.getZ()), 2);
        return Math.sqrt(x + y + z);
    }

}
