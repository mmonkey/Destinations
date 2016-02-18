package com.github.mmonkey.Destinations.Migrations;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Migrations.ConfigMigrations.M01_AddDatabaseSettings;
import com.github.mmonkey.Destinations.Migrations.ConfigMigrations.M02_AddCallBringSettings;
import com.github.mmonkey.Destinations.Migrations.ConfigMigrations.M03_UpdateExpiresAfterToSeconds;

public class ConfigMigrationRunner extends MigrationRunner {

    protected MigrationInterface getMigration(int version) {

        switch (version) {
            case 0:
                return new M01_AddDatabaseSettings(plugin);

            case 1:
                return new M02_AddCallBringSettings(plugin);

            case 2:
                return new M03_UpdateExpiresAfterToSeconds(plugin);

            default:
                return null;
        }
    }

    public ConfigMigrationRunner(Destinations plugin, int version) {
        super(plugin, version);
        this.latest = Destinations.CONFIG_VERSION;
    }
}
