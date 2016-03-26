package com.initianovamc.rysingdragon.landprotect.config;

import com.initianovamc.rysingdragon.landprotect.LandProtect;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GeneralConfig implements Configuration{
	
	private static GeneralConfig generalConfig = new GeneralConfig();
	private Path configFile = LandProtect.instance.getConfigDir().resolve("config.conf");
	private ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();
	private CommentedConfigurationNode configNode;

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
		List<String> interactableBlocks = new ArrayList<>();
		interactableBlocks.add("minecraft:chest");
		interactableBlocks.add("minecraft:lever");
		configNode.getNode("Interactable").setValue(interactableBlocks).setComment("blocks that are interactable in protected land");
	}

	@Override
	public CommentedConfigurationNode getConfigNode() {
		return configNode;
	}

	public static GeneralConfig getConfig() {
		return generalConfig;
	}
	
}
