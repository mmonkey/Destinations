package com.github.mmonkey.Destinations.Migrations.ConfigMigrations;

import com.github.mmonkey.Destinations.Configs.DefaultConfig;
import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Migrations.Migration;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class M03_UpdateExpiresAfterToSeconds extends Migration {

    public void up() {
        CommentedConfigurationNode config = this.plugin.getDefaultConfig().get();

        int time = config.getNode(DefaultConfig.TELEPORT_SETTINGS, DefaultConfig.EXPIRES_AFTER).getInt(1);
        time = (time == 1) ? 30 : time * 60;

        config.getNode(DefaultConfig.TELEPORT_SETTINGS, DefaultConfig.EXPIRES_AFTER).setValue(time).setComment("Number of seconds before call requests expire");
        this.plugin.getDefaultConfig().save();

        this.bumpVersion(DefaultConfig.CONFIG_VERSION, 3);
    }

    public M03_UpdateExpiresAfterToSeconds(Destinations plugin) {
        super(plugin);
    }
}
