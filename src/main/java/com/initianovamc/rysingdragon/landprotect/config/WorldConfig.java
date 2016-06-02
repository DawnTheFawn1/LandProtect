package com.initianovamc.rysingdragon.landprotect.config;

import com.initianovamc.rysingdragon.landprotect.LandProtect;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WorldConfig implements Configuration {

	private static WorldConfig worldConfig = new WorldConfig();
	private Path configFile = LandProtect.instance.getConfigDir().resolve("worlds.conf");
	private ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();
	private CommentedConfigurationNode configNode;
	
	@Override
	public void setup() {
		if (!configFile.toFile().exists()) {
			try {
				configFile.toFile().createNewFile();
				load();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			load();
		}
	}

	@Override
	public void save() {
		try {
			configLoader.save(configNode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load() {
		try {
			configNode = configLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setDefaults() {
	
	}
	
	public static WorldConfig getConfig() {
		return worldConfig;
	}

	@Override
	public CommentedConfigurationNode getConfigNode() {
		return configNode;
	}

}
