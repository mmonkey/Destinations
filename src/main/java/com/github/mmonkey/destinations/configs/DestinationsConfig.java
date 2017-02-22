package com.github.mmonkey.destinations.configs;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.io.File;
import java.io.IOException;

public class DestinationsConfig extends Config {

    public static final String DATABASE_SETTINGS = "Database Settings";
    public static final String COMMAND_SETTINGS = "Command Settings";
    public static final String ECONOMY_SETTINGS = "Economy Settings";

    private static DestinationsConfig instance;

    /**
     * DestinationsConfig constructor
     *
     * @param directory File
     * @param filename  String
     * @throws IOException maybe thrown if there was an error loading the file, or creating the file for the first time.
     */
    public DestinationsConfig(File directory, String filename) throws IOException {
        super(directory, filename);

        instance = this;
    }

    /**
     * @return the DestinationsConfig instance
     */
    public static DestinationsConfig getInstance() {
        return instance;
    }

    @Override
    protected void setDefaults() {
        get().getNode(DATABASE_SETTINGS, "type").setValue("H2").setComment("Accepted Types: H2");
        get().getNode(DATABASE_SETTINGS, "url").setValue("jdbc:h2:file:." + File.separator + "destinations" + File.separator);
        get().getNode(DATABASE_SETTINGS, "database").setValue("data");
        get().getNode(DATABASE_SETTINGS, "username").setValue("");
        get().getNode(DATABASE_SETTINGS, "password").setValue("");

        get().getNode(COMMAND_SETTINGS, "saveBackLocationOnDeath").setValue(true);
        get().getNode(COMMAND_SETTINGS, "maximumHomes").setValue(0).setComment("Setting this to 0 will allow unlimited homes.");
        get().getNode(COMMAND_SETTINGS, "teleportRequestExpiration").setValue(30).setComment("Time (seconds) before teleport requests expire.");

        CommentedConfigurationNode econ = get().getNode(ECONOMY_SETTINGS);
        econ.getNode("enabled").setValue(false);
        econ.getNode("distance").setValue(100).setComment("When calculating variable cost: A: the distance between player location and teleport location." +
                "B: this distance. C: the location type rate (see below). Formula: ( A / B ) * C = COST.");

        econ.getNode("locations", "back", "rate").setValue(0);
        econ.getNode("locations", "back", "type").setValue("fixed").setComment("Accepted Types: fixed, variable, none");
        econ.getNode("locations", "back", "cooldown").setValue(60).setComment("Time (seconds) before teleport to this location for no cost.");

        econ.getNode("locations", "bed", "rate").setValue(0);
        econ.getNode("locations", "bed", "type").setValue("fixed").setComment("Accepted Types: fixed, variable, none");
        econ.getNode("locations", "bed", "cooldown").setValue(60).setComment("Time (seconds) before teleport to this location for no cost.");

        econ.getNode("locations", "home", "rate").setValue(0);
        econ.getNode("locations", "home", "type").setValue("fixed").setComment("Accepted Types: fixed, variable, none");
        econ.getNode("locations", "home", "cooldown").setValue(60).setComment("Time (seconds) before teleport to this location for no cost.");

        econ.getNode("locations", "jump", "rate").setValue(0);
        econ.getNode("locations", "jump", "type").setValue("fixed").setComment("Accepted Types: fixed, variable, none");
        econ.getNode("locations", "jump", "cooldown").setValue(60).setComment("Time (seconds) before teleport to this location for no cost.");

        econ.getNode("locations", "spawn", "rate").setValue(0);
        econ.getNode("locations", "spawn", "type").setValue("fixed").setComment("Accepted Types: fixed, variable, none");
        econ.getNode("locations", "spawn", "cooldown").setValue(60).setComment("Time (seconds) before teleport to this location for no cost.");

        econ.getNode("locations", "top", "rate").setValue(0);
        econ.getNode("locations", "top", "type").setValue("fixed").setComment("Accepted Types: fixed, variable, none");
        econ.getNode("locations", "top", "cooldown").setValue(60).setComment("Time (seconds) before teleport to this location for no cost.");

        econ.getNode("locations", "warp", "rate").setValue(0);
        econ.getNode("locations", "warp", "type").setValue("fixed").setComment("Accepted Types: fixed, variable, none");
        econ.getNode("locations", "warp", "cooldown").setValue(60).setComment("Time (seconds) before teleport to this location for no cost.");
    }

    /**
     * @return whether or not the player's back locations will be saved on death
     */
    public static boolean allowBackOnDeath() {
        return getInstance().get().getNode(COMMAND_SETTINGS, "saveBackLocationOnDeath").getBoolean(true);
    }

    /**
     * @return the maximum number of homes for any player
     */
    public static int getMaximumHomes() {
        return getInstance().get().getNode(COMMAND_SETTINGS, "maximumHomes").getInt(0);
    }

    /**
     * @return the request expiration time for teleport request (in seconds)
     */
    public static int getTeleportRequestExpiration() {
        return getInstance().get().getNode(COMMAND_SETTINGS, "teleportRequestExpiration").getInt(30);
    }

    /**
     * @return whether or not economy support has been enabled
     */
    public static boolean isEconomyEnabled() {
        return getInstance().get().getNode(ECONOMY_SETTINGS, "enabled").getBoolean(false);
    }

    /**
     * @param locationType String type of location
     * @return The cost type used to calculate the teleport cost
     */
    public static String getCostType(String locationType) {
        return getInstance().get().getNode(ECONOMY_SETTINGS, "locations", locationType, "type").getString("fixed");
    }

    /**
     * @param locationType String type of location
     * @return The rate used to calculate the teleport cost
     */
    public static double getRate(String locationType) {
        return getInstance().get().getNode(ECONOMY_SETTINGS, "locations", locationType, "rate").getDouble(0);
    }

    /**
     * @param locationType String type of location
     * @return The cooldown used to calculate the teleport cost
     */
    public static int getCooldown(String locationType) {
        return getInstance().get().getNode(ECONOMY_SETTINGS, "locations", locationType, "cooldown").getInt(60);
    }

}
