package com.github.mmonkey.Destinations.Migrations;

import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Destinations;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class AddCallBringSettingsToDefaultConfig implements Migration {

    private Destinations plugin;

    public void up() {
        CommentedConfigurationNode config = plugin.getDefaultConfig().get();
        config.getNode(DefaultConfig.CALL_BRING_SETTINGS, DefaultConfig.ENABLED).setValue(true);
        plugin.getDefaultConfig().save();
    }

    public void down() {
        CommentedConfigurationNode config = plugin.getDefaultConfig().get();
        config.removeChild(DefaultConfig.CALL_BRING_SETTINGS);
        plugin.getDefaultConfig().save();
    }

    public AddCallBringSettingsToDefaultConfig(Destinations plugin) {
        this.plugin = plugin;
    }
}
