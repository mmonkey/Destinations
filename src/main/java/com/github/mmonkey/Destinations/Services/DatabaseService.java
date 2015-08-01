package com.github.mmonkey.Destinations.Services;

import com.github.mmonkey.Destinations.Database.DatabaseConnection;

public class DatabaseService {

    private DatabaseConnection databaseConnection;

    protected DatabaseConnection getDatabaseConnection() {
        return this.databaseConnection;
    }

    public DatabaseService(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
}
