package com.github.mmonkey.Destinations.Migrations;

import com.github.mmonkey.Destinations.Destinations;

abstract class MigrationRunner {

    protected Destinations plugin;
    protected int version;
    protected int latest = 0;

    public void run() {

        while (this.version != this.latest) {
            MigrationInterface migration = this.getMigration(this.version);

            if (migration != null) {
                migration.up();
                this.version++;
            }
        }

    }

    abstract MigrationInterface getMigration(int version);

    public MigrationRunner(Destinations plugin, int version) {
        this.plugin = plugin;
        this.version = version;
    }
}
