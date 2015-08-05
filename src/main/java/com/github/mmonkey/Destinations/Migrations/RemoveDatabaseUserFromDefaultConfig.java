package com.github.mmonkey.Destinations.Migrations;

import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Destinations;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class RemoveDatabaseUserFromDefaultConfig implements Migration {

    private Destinations plugin;

    public void up() {
        CommentedConfigurationNode config = plugin.getDefaultConfig().get();
        config.getNode(DefaultConfig.CONFIG_VERSION).setValue(3);
        config.getNode(DefaultConfig.DATABASE_SETTINGS).removeChild("username");
        config.getNode(DefaultConfig.DATABASE_SETTINGS).removeChild("password");
        plugin.getDefaultConfig().save();
    }

    public RemoveDatabaseUserFromDefaultConfig(Destinations plugin) {
        this.plugin = plugin;
    }

}
