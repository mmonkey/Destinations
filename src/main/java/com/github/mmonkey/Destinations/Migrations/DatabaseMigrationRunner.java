package com.github.mmonkey.Destinations.Migrations;

import com.github.mmonkey.Destinations.Destinations;
import com.github.mmonkey.Destinations.Migrations.DatabaseMigrations.M01_AddInitialDatabaseTables;

public class DatabaseMigrationRunner extends MigrationRunner {

    protected MigrationInterface getMigration(int version) {

        switch (version) {
            case 0:
                return new M01_AddInitialDatabaseTables(this.plugin);

            default:
                return null;
        }

    }

    public DatabaseMigrationRunner(Destinations plugin, int version) {
        super(plugin, version);
        this.latest = Destinations.DATABASE_VERSION;
    }

}
