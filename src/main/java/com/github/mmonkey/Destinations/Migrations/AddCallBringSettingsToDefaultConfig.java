package com.github.mmonkey.Destinations.Migrations;

import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Destinations;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class AddCallBringSettingsToDefaultConfig implements Migration {

    private Destinations plugin;

    public void up() {
        CommentedConfigurationNode config = plugin.getDefaultConfig().get();
        config.getNode(DefaultConfig.CONFIG_VERSION).setValue(2);
        config.getNode(DefaultConfig.DATABASE_VERSION).setValue(1);
        config.getNode(DefaultConfig.TELEPORT_SETTINGS, DefaultConfig.ENABLED).setValue(true);
        config.getNode(DefaultConfig.TELEPORT_SETTINGS, DefaultConfig.EXPIRES_AFTER).setValue(1).setComment("Time (in minutes) that teleport requests last");
        plugin.getDefaultConfig().save();
    }

    public void down() {
        CommentedConfigurationNode config = plugin.getDefaultConfig().get();
        config.getNode(DefaultConfig.CONFIG_VERSION).setValue(1);
        config.getNode(DefaultConfig.DATABASE_VERSION).setValue(0);
        config.removeChild(DefaultConfig.TELEPORT_SETTINGS);
        plugin.getDefaultConfig().save();
    }

    public AddCallBringSettingsToDefaultConfig(Destinations plugin) {
        this.plugin = plugin;
    }
}
