package com.github.mmonkey.Destinations.Migrations.ConfigMigrations;

import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Migrations.Migration;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class M02_AddCallBringSettings extends Migration {

    public void up() {
        CommentedConfigurationNode config = this.plugin.getDefaultConfig().get();
        config.getNode(DefaultConfig.TELEPORT_SETTINGS, DefaultConfig.ENABLED).setValue(true);
        config.getNode(DefaultConfig.TELEPORT_SETTINGS, DefaultConfig.EXPIRES_AFTER).setValue(1).setComment("Time (in minutes) that teleport requests last");
        this.plugin.getDefaultConfig().save();

        this.bumpVersion(DefaultConfig.CONFIG_VERSION, 2);
    }

    public M02_AddCallBringSettings(Destinations plugin) {
        super(plugin);
    }
}
