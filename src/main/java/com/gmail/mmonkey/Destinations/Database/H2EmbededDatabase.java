package com.gmail.mmonkey.Destinations.Database;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.h2.tools.Server;
import org.spongepowered.api.Game;

public class H2EmbededDatabase extends Database {

	private static final String jdbcH2EmbededUrl = "jdbc:h2:file:";
	private static final String defaultPath = "." + File.separator + "data";
	
	private String dbName;
	private String username;
	private String password;
	private File directory = null;
	private Server webServer;
	private Server tcpServer;
	
	public boolean setDirectory(File directory) {
		
		if(directory.exists() && directory.isDirectory()) {
			this.directory = directory;
			return true;
		}
		
		return false;
	}
	
	public Connection getConnection() {
		
		String path = (this.directory != null && this.directory.exists()) ? this.directory.getPath() : defaultPath;
		
		this.setUsername(this.username);
		this.setPassword(this.password);
		this.setJdbcUrl(jdbcH2EmbededUrl + path + File.separator + this.dbName + ";user=" + this.username + ";password=" + this.password);
		return super.getConnection();
		
	}
	
	public boolean startWebServer() {
		
		try {
		
			this.webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
			this.tcpServer = Server.createTcpServer("-tcp", "-tcpAllowOthers", "true", "-tcpPort", "9092", "-tcpPassword", "test123").start();
			return true;
		
		} catch (SQLException e) {
			
			return false;
		
		}
	}
	
	public void stopWebServer() {
		
		this.webServer.stop();
		this.tcpServer.stop();
	
	}
	
	public H2EmbededDatabase(Game game, String dbName, String username, String password) {
		super(game);
		this.dbName = dbName;
		this.username = username;
		this.password = password;
	}

}
