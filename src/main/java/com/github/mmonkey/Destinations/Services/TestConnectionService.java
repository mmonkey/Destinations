package com.github.mmonkey.Destinations.Services;

import com.github.mmonkey.Destinations.Dams.TestConnectionDam;
import com.github.mmonkey.Destinations.Database.Database;

public class TestConnectionService extends DatabaseService {

    private TestConnectionDam testConnectionDam;

    public boolean execute()
    {
        return this.testConnectionDam.testConnection();
    }

    public TestConnectionService(Database database) {
        super(database);
        this.testConnectionDam = new TestConnectionDam(this.getDatabase());
    }
}
