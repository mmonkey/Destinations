package com.github.mmonkey.Destinations.Database;

import org.spongepowered.api.Game;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {
	
	private Game game;
	private SqlService service;
	private String jdbcUrl = null;
	
	private String getJdbcUrl() {
		return (this.jdbcUrl != null) ? this.jdbcUrl : "";
	}
	
	protected void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	
	public DataSource getDataSource(String jdbcUrl) {
		
		DataSource source = null;
		
		if (this.service == null) {
			this.service = this.game.getServiceManager().provide(SqlService.class).get();
		}
		
		try {
			source =  this.service.getDataSource(jdbcUrl);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return source;
	}
	
	public Connection getConnection() {
		
		try {
			return this.getDataSource(this.getJdbcUrl()).getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	protected Database(Game game) {
		this.game = game;
	}

}
