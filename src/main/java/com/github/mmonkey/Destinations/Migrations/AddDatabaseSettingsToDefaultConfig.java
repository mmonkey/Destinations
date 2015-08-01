package com.github.mmonkey.Destinations.Migrations;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Services.DefaultConfigStorageService;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class AddDatabaseSettingsToDefaultConfig implements Migration {

    private Destinations plugin;

    public void up() {
        CommentedConfigurationNode config = plugin.getDefaultConfigService().getConfig();
        config.getNode(DefaultConfigStorageService.VERSION).setValue(1);
        config.getNode(DefaultConfigStorageService.DATABASE_SETTINGS, DefaultConfigStorageService.PASSWORD).setValue("");
        config.getNode(DefaultConfigStorageService.DATABASE_SETTINGS, DefaultConfigStorageService.USERNAME).setValue("admin");
        config.getNode(DefaultConfigStorageService.DATABASE_SETTINGS, DefaultConfigStorageService.WEBSERVER).setValue(false);
        plugin.getDefaultConfigService().save();
    }

    public void down() {
        CommentedConfigurationNode config = plugin.getDefaultConfigService().getConfig();
        config.getNode(DefaultConfigStorageService.VERSION).setValue(0);
        config.removeChild(DefaultConfigStorageService.DATABASE_SETTINGS);
        plugin.getDefaultConfigService().save();
    }

    public AddDatabaseSettingsToDefaultConfig(Destinations plugin) {
        this.plugin = plugin;
    }
}
