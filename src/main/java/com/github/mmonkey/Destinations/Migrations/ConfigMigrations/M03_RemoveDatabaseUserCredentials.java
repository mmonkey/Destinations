package com.github.mmonkey.Destinations.Migrations.ConfigMigrations;

import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Migrations.MigrationInterface;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class M03_RemoveDatabaseUserCredentials implements MigrationInterface {

    private Destinations plugin;

    public void up() {
        CommentedConfigurationNode config = plugin.getDefaultConfig().get();
        config.getNode(DefaultConfig.CONFIG_VERSION).setValue(3);
        config.getNode(DefaultConfig.DATABASE_SETTINGS).removeChild("username");
        config.getNode(DefaultConfig.DATABASE_SETTINGS).removeChild("password");
        plugin.getDefaultConfig().save();
    }

    public M03_RemoveDatabaseUserCredentials(Destinations plugin) {
        this.plugin = plugin;
    }

}
