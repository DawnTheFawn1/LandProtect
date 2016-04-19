package com.initianovamc.rysingdragon.landprotect.config;

import com.initianovamc.rysingdragon.landprotect.LandProtect;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;

public class PlayerConfig implements Configuration{

	private Path configFile = LandProtect.instance.getConfigDir().resolve("playerdata.conf");
	private ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();
	private CommentedConfigurationNode configNode;
	private static PlayerConfig playerConfig = new PlayerConfig();
	
	@Override
	public void setup() {
		if (!configFile.toFile().exists()) {
			try {
				configFile.toFile().createNewFile();
				load();
				setDefaults();
				save();
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
		configNode.getNode("friendsList").setComment("list of everybody's friends who they share all claims with.");
		configNode.getNode("registeredPlayers").setComment("list of all registered player UUID's by LandProtect");
	}

	@Override
	public CommentedConfigurationNode getConfigNode() {
		return configNode;
	}

	public static PlayerConfig getPlayerConfig() {
		return playerConfig;
	}
	
}