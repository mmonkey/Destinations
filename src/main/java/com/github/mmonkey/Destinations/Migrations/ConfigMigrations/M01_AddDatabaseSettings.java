package com.github.mmonkey.Destinations.Migrations.ConfigMigrations;

import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Migrations.Migration;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class M01_AddDatabaseSettings extends Migration {

    public void up() {
        CommentedConfigurationNode config = this.plugin.getDefaultConfig().get();
        config.getNode(DefaultConfig.DATABASE_SETTINGS, DefaultConfig.WEBSERVER).setValue(false);
        this.plugin.getDefaultConfig().save();

        this.bumpVersion(DefaultConfig.CONFIG_VERSION, 1);
    }

    public M01_AddDatabaseSettings(Destinations plugin) {
        super(plugin);
    }
}
