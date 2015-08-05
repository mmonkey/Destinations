package com.github.mmonkey.Destinations.Migrations;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class AddDatabaseSettingsToDefaultConfig implements Migration {

    private Destinations plugin;

    public void up() {
        CommentedConfigurationNode config = plugin.getDefaultConfig().get();
        config.getNode(DefaultConfig.CONFIG_VERSION).setValue(1);
        config.getNode(DefaultConfig.DATABASE_SETTINGS, "password").setValue("");
        config.getNode(DefaultConfig.DATABASE_SETTINGS, "username").setValue("admin");
        config.getNode(DefaultConfig.DATABASE_SETTINGS, DefaultConfig.WEBSERVER).setValue(false);
        plugin.getDefaultConfig().save();
    }

    public AddDatabaseSettingsToDefaultConfig(Destinations plugin) {
        this.plugin = plugin;
    }
}
