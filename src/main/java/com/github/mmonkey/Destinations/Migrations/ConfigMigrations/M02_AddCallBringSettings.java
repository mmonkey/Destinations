package com.github.mmonkey.Destinations.Migrations.ConfigMigrations;

import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Migrations.MigrationInterface;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class M02_AddCallBringSettings implements MigrationInterface {

    private Destinations plugin;

    public void up() {
        CommentedConfigurationNode config = plugin.getDefaultConfig().get();
        config.getNode(DefaultConfig.CONFIG_VERSION).setValue(2);
        config.getNode(DefaultConfig.DATABASE_VERSION).setValue(1);
        config.getNode(DefaultConfig.TELEPORT_SETTINGS, DefaultConfig.ENABLED).setValue(true);
        config.getNode(DefaultConfig.TELEPORT_SETTINGS, DefaultConfig.EXPIRES_AFTER).setValue(1).setComment("Time (in minutes) that teleport requests last");
        plugin.getDefaultConfig().save();
    }

    public M02_AddCallBringSettings(Destinations plugin) {
        this.plugin = plugin;
    }
}
