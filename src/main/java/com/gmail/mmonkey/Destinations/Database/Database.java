package com.gmail.mmonkey.Destinations.Database;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.spongepowered.api.Game;
import org.spongepowered.api.service.sql.SqlService;

abstract class Database {
	
	private Game game;
	private SqlService service;
	private String username = null;
	private String password = null;
	private String jdbcUrl = null;
	
	private String getUsername() {
		return (this.username != null) ? this.username : "";
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	private String getPassword() {
		return (this.password != null) ? this.password : "";
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	private String getJdbcUrl() {
		return (this.jdbcUrl != null) ? this.jdbcUrl : "";
	}
	
	protected void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	
	protected DataSource getDataSource(String jdbcUrl) {
		
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
	
	protected Connection getConnection() {
		
		try {
			return this.getDataSource(this.getJdbcUrl()).getConnection(this.getUsername(), this.getPassword());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	protected Database(Game game) {
		this.game = game;
	}

}
