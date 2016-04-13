package com.initianovamc.rysingdragon.landprotect.config;

import com.initianovamc.rysingdragon.landprotect.LandProtect;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.item.ItemTypes;

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
		configNode.getNode("Interactable").setValue(interactableBlocks).setComment("blocks that are interactable in protected land");
		configNode.getNode("InspectTool").setValue(ItemTypes.WOODEN_AXE.getId()).setComment("Item the player needs to have in their hand to inspect claim boundaries");
		configNode.getNode("BoundaryBlock").setValue(BlockTypes.COAL_BLOCK.getId()).setComment("Block that will appear as claim boundary when claiming land or using the inspect tool");
	}

	@Override
	public CommentedConfigurationNode getConfigNode() {
		return configNode;
	}

	public static GeneralConfig getConfig() {
		return generalConfig;
	}
	
}
