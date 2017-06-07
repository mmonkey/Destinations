package com.github.mmonkey.destinations.configs;

import java.io.File;
import java.io.IOException;

public class DestinationsConfig extends Config {

    public static final String DATABASE_SETTINGS = "Database Settings";
    public static final String COMMAND_SETTINGS = "Command Settings";

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
        get().getNode(DATABASE_SETTINGS, "type").setValue("H2").setComment("Accepted Types: H2, MySQL");
        get().getNode(DATABASE_SETTINGS, "url").setValue("jdbc:h2:file:." + File.separator + "config" + File.separator + "destinations" + File.separator);
        get().getNode(DATABASE_SETTINGS, "database").setValue("destinations");
        get().getNode(DATABASE_SETTINGS, "username").setValue("");
        get().getNode(DATABASE_SETTINGS, "password").setValue("");

        get().getNode(COMMAND_SETTINGS, "saveBackLocationOnDeath").setValue(true);
        get().getNode(COMMAND_SETTINGS, "maximumHomes").setValue(0).setComment("Setting this to 0 will allow unlimited homes.");
        get().getNode(COMMAND_SETTINGS, "teleportRequestExpiration").setValue(30).setComment("Number of seconds before teleport requests expire.");
    }

    /**
     * @return whether or not the player's back locations will be saved on death
     */
    public static boolean allowBackOnDeath() {
        return getInstance().get().getNode(COMMAND_SETTINGS, "saveBackLocationOnDeath").getBoolean(false);
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

}
