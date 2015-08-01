package com.github.mmonkey.Destinations.Services;

import com.github.mmonkey.Destinations.Database.Database;

public class DatabaseService {

    private Database database;

    protected Database getDatabase() {
        return this.database;
    }

    public DatabaseService(Database database) {
        this.database = database;
    }
}
