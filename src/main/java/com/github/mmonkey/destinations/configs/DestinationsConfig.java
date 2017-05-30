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
        get().getNode(DATABASE_SETTINGS).setComment("Accepted Database Types: H2");
        get().getNode(DATABASE_SETTINGS, "type").setValue("H2");
        get().getNode(DATABASE_SETTINGS, "url").setValue("jdbc:h2:file:." + File.separator + "destinations" + File.separator);
        get().getNode(DATABASE_SETTINGS, "database").setValue("data");
        get().getNode(DATABASE_SETTINGS, "username").setValue("");
        get().getNode(DATABASE_SETTINGS, "password").setValue("");

        get().getNode(COMMAND_SETTINGS, "maximumHomes").setValue(0).setComment("Setting this to 0 will allow unlimited homes.");
        get().getNode(COMMAND_SETTINGS, "saveBackLocationOnDeath").setValue(true);
        get().getNode(COMMAND_SETTINGS, "teleportRequestExpiration").setValue(30).setComment("Time (seconds) before teleport requests expire.");

        CommentedConfigurationNode econ = get().getNode(ECONOMY_SETTINGS);
        econ.setComment("For more information about Economy Settings, see the readme: https://github.com/mmonkey/Destinations");
        econ.getNode("enabled").setValue(false);

        econ.getNode("locationTypes", "back", "rate").setValue(1.00);
        econ.getNode("locationTypes", "back", "type").setValue("fixed");

        econ.getNode("locationTypes", "bed", "rate").setValue(1.00);
        econ.getNode("locationTypes", "bed", "type").setValue("fixed");

        econ.getNode("locationTypes", "home", "rate").setValue(1.00);
        econ.getNode("locationTypes", "home", "type").setValue("fixed");

        econ.getNode("locationTypes", "jump", "rate").setValue(1.00);
        econ.getNode("locationTypes", "jump", "type").setValue("fixed");

        econ.getNode("locationTypes", "spawn", "rate").setValue(1.00);
        econ.getNode("locationTypes", "spawn", "type").setValue("fixed");

        econ.getNode("locationTypes", "top", "rate").setValue(1.00);
        econ.getNode("locationTypes", "top", "type").setValue("fixed");

        econ.getNode("locationTypes", "warp", "rate").setValue(1.00);
        econ.getNode("locationTypes", "warp", "type").setValue("fixed");
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
    public static String getLocationTypeEconomyType(String locationType) {
        return getInstance().get().getNode(ECONOMY_SETTINGS, "locationTypes", locationType, "type").getString("fixed");
    }

    /**
     * @param locationType String type of location
     * @return The rate used to calculate the teleport cost
     */
    public static double getLocationTypeEconomyRate(String locationType) {
        return getInstance().get().getNode(ECONOMY_SETTINGS, "locationTypes", locationType, "rate").getDouble(0);
    }

}
