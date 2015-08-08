package com.github.mmonkey.Destinations.Migrations.ConfigMigrations;

import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Migrations.Migration;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class M03_RemoveDatabaseUserCredentials extends Migration {

    public void up() {
        CommentedConfigurationNode config = this.plugin.getDefaultConfig().get();
        config.getNode(DefaultConfig.DATABASE_SETTINGS).removeChild("username");
        config.getNode(DefaultConfig.DATABASE_SETTINGS).removeChild("password");
        this.plugin.getDefaultConfig().save();

        this.bumpVersion(DefaultConfig.CONFIG_VERSION, 3);
    }

    public M03_RemoveDatabaseUserCredentials(Destinations plugin) {
        super(plugin);
    }

}
