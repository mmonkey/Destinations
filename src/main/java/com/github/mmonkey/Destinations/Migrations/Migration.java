package com.github.mmonkey.Destinations.Migrations;

import com.github.mmonkey.Destinations.Destinations;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public abstract class Migration implements MigrationInterface {

    protected Destinations plugin;

    protected void bumpVersion(String node, int version) {
        CommentedConfigurationNode config = plugin.getDefaultConfig().get();
        config.getNode(node).setValue(version);
        plugin.getDefaultConfig().save();
    }

    public Migration(Destinations plugin) {
        this.plugin = plugin;
    }

}
