package com.github.mmonkey.destinations.configs;

import java.io.File;
import java.io.IOException;

public class DestinationsConfig extends Config {

    public static final String DATABASE_SETTINGS = "Database Settings";
    public static final String BACK_SETTINGS = "Back Settings";
    public static final String HOME_SETTINGS = "Home Settings";
    public static final String TELEPORT_SETTINGS = "Teleport Settings";
    public static final String WARP_SETTINGS = "Warp Settings";

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

        get().getNode(BACK_SETTINGS, "enabled").setValue(true);
        get().getNode(BACK_SETTINGS, "saveOnDeath").setValue(true);

        get().getNode(HOME_SETTINGS, "enabled").setValue(true);
        get().getNode(HOME_SETTINGS, "maxHomes").setValue(0);

        get().getNode(TELEPORT_SETTINGS, "enabled").setValue(true);
        get().getNode(TELEPORT_SETTINGS, "expires").setValue(30).setComment("Number of seconds before teleport requests expire.");

        get().getNode(WARP_SETTINGS, "enabled").setValue(true);
    }

    /**
     * @return whether or not the back command is enabled
     */
    public static boolean isBackCommandEnabled() {
        return getInstance().get().getNode(BACK_SETTINGS, "enabled").getBoolean(false);
    }

    /**
     * @return whether or not the player's back locations will be saved on death
     */
    public static boolean allowBackOnDeath() {
        return getInstance().get().getNode(BACK_SETTINGS, "saveOnDeath").getBoolean(false);
    }

    /**
     * @return whether or not the home command is enabled
     */
    public static boolean isHomeCommandEnabled() {
        return getInstance().get().getNode(HOME_SETTINGS, "enabled").getBoolean(false);
    }

    /**
     * @return whether or not the teleport command is enabled
     */
    public static boolean isTeleportCommandEnabled() {
        return getInstance().get().getNode(TELEPORT_SETTINGS, "enabled").getBoolean(false);
    }

    /**
     * @return whether or not the warp command is enabled
     */
    public static boolean isWarpCommandEnabled() {
        return getInstance().get().getNode(WARP_SETTINGS, "enabled").getBoolean(false);
    }

}
