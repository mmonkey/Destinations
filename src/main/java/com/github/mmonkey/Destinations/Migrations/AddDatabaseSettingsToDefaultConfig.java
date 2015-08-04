package com.github.mmonkey.Destinations.Migrations;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class AddDatabaseSettingsToDefaultConfig implements Migration {

    private Destinations plugin;

    public void up() {
        CommentedConfigurationNode config = plugin.getDefaultConfig().getConfig();
        config.getNode(DefaultConfig.VERSION).setValue(1);
        config.getNode(DefaultConfig.DATABASE_SETTINGS, DefaultConfig.PASSWORD).setValue("");
        config.getNode(DefaultConfig.DATABASE_SETTINGS, DefaultConfig.USERNAME).setValue("admin");
        config.getNode(DefaultConfig.DATABASE_SETTINGS, DefaultConfig.WEBSERVER).setValue(false);
        plugin.getDefaultConfig().saveConfig();
    }

    public void down() {
        CommentedConfigurationNode config = plugin.getDefaultConfig().getConfig();
        config.getNode(DefaultConfig.VERSION).setValue(0);
        config.removeChild(DefaultConfig.DATABASE_SETTINGS);
        plugin.getDefaultConfig().saveConfig();
    }

    public AddDatabaseSettingsToDefaultConfig(Destinations plugin) {
        this.plugin = plugin;
    }
}
