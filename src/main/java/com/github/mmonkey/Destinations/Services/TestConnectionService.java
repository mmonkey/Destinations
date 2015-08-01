package com.github.mmonkey.Destinations.Services;

import com.github.mmonkey.Destinations.Dams.TestConnectionDam;
import com.github.mmonkey.Destinations.Database.DatabaseConnection;

public class TestConnectionService extends DatabaseService {

    private TestConnectionDam testConnectionDam;

    public boolean execute()
    {
        return this.testConnectionDam.testConnection();
    }

    public TestConnectionService(DatabaseConnection databaseConnection) {
        super(databaseConnection);
        this.testConnectionDam = new TestConnectionDam(this.getDatabaseConnection());
    }
}
