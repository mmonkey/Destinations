package com.github.mmonkey.destinations.configs;

import com.google.common.base.Preconditions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;

public abstract class Config {

	private final File file;
	private final ConfigurationLoader<CommentedConfigurationNode> loader;
	private final CommentedConfigurationNode config;

	/**
	 * Config constructor
	 *
	 * @param directory File
	 * @param filename  String
	 * @throws IOException maybe thrown if there was an error loading the file, or creating the file for the first time.
	 */
	Config(File directory, String filename) throws IOException {
		Preconditions.checkNotNull(directory);
		Preconditions.checkNotNull(filename);

		this.file = new File(directory, filename);
		this.loader = HoconConfigurationLoader.builder().setFile(this.file).build();

		if (!this.file.isFile() && this.file.createNewFile()) {
			this.config = this.loader.load();
			this.setDefaults();
			this.save();
		} else {
			this.config = this.loader.load();
		}
	}

	/**
	 * Get this config's file
	 *
	 * @return File
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * Get the main config node
	 *
	 * @return CommentedConfigurationNode
	 */
	public CommentedConfigurationNode get() {
		return this.config;
	}

	/**
	 * Save this config
	 */
	public void save() {
		try {
			this.loader.save(this.get());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set default values the first time the config file is generated
	 */
	protected abstract void setDefaults();

}
