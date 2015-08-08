package com.github.mmonkey.Destinations.Migrations.ConfigMigrations;

import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Migrations.MigrationInterface;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class M04_UpdateExpiresAfterToSeconds implements MigrationInterface {

    private Destinations plugin;

    public void up() {
        CommentedConfigurationNode config = plugin.getDefaultConfig().get();
        config.getNode(DefaultConfig.CONFIG_VERSION).setValue(4);

        int time = config.getNode(DefaultConfig.TELEPORT_SETTINGS, DefaultConfig.EXPIRES_AFTER).getInt(1);
        time = (time == 1) ? 30 : time * 60;

        config.getNode(DefaultConfig.TELEPORT_SETTINGS, DefaultConfig.EXPIRES_AFTER).setValue(time).setComment("Number of seconds before call requests expire");
        plugin.getDefaultConfig().save();
    }

    public M04_UpdateExpiresAfterToSeconds(Destinations plugin) {
        this.plugin = plugin;
    }
}
