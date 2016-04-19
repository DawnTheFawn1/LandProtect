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
			checkValues();
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
		configNode.getNode("Interactable").setValue(new ArrayList<>()).setComment("blocks that are interactable in protected land");
		configNode.getNode("InspectTool").setValue(ItemTypes.WOODEN_AXE.getId()).setComment("Item the player needs to have in their hand to inspect claim boundaries");
		configNode.getNode("BoundaryBlock").setValue(BlockTypes.COAL_BLOCK.getId()).setComment("Block that will appear as claim boundary when claiming land or using the inspect tool");
		configNode.getNode("MySQL").setComment("Settings for using MySQL to save data");
		configNode.getNode("MySQL", "enabled").setValue(false);
		configNode.getNode("MySQL", "host").setValue("localhost");
		configNode.getNode("MySQL", "port").setValue(3306);
		configNode.getNode("MySQL", "database").setValue("LandProtect");
		configNode.getNode("MySQL", "username").setValue("user");
		configNode.getNode("MySQL", "password").setValue("pass");
		configNode.getNode("DataTransfer").setValue(false).setComment("Transfers claims and friends to the newer, more optimized system. Claims and friends will no longer be saved in configs even if this is false");
	}

	@Override
	public CommentedConfigurationNode getConfigNode() {
		return configNode;
	}

	public static GeneralConfig getConfig() {
		return generalConfig;
	}

	public void checkValues() {
		if (configNode.getNode("Interactable").getValue() == null) configNode.getNode("Interactable").setValue(new ArrayList<>()).setComment("blocks that are interactable in protected land");
		if (configNode.getNode("InspectTool").getValue() == null) configNode.getNode("InspectTool").setValue(ItemTypes.WOODEN_AXE.getId()).setComment("Item the player needs to have in their hand to inspect claim boundaries");
		if (configNode.getNode("BoundaryBlock").getValue() == null) configNode.getNode("BoundaryBlock").setValue(BlockTypes.COAL_BLOCK.getId()).setComment("Block that will appear as claim boundary when claiming land or using the inspect tool");
		if (configNode.getNode("MySQL").isVirtual()) configNode.getNode("MySQL").setComment("Settings for using MySQL to save data");
		if (configNode.getNode("MySQL", "enabled").getValue() == null) configNode.getNode("MySQL", "enabled").setValue(false);
		if (configNode.getNode("MySQL", "host").getValue() == null) configNode.getNode("MySQL", "host").setValue("localhost");
		if (configNode.getNode("MySQL", "port").getValue() == null) configNode.getNode("MySQL", "port").setValue(3306);
		if (configNode.getNode("MySQL", "database").getValue() == null) configNode.getNode("MySQL", "database").setValue("LandProtect");
		if (configNode.getNode("MySQL", "username").getValue() == null) configNode.getNode("MySQL", "username").setValue("user");
		if (configNode.getNode("MySQL", "password").getValue() == null) configNode.getNode("MySQL", "password").setValue("pass");	
		if (configNode.getNode("DataTransfer").getValue() == null) configNode.getNode("DataTransfer").setValue(false).setComment("Transfers claims and friends to the newer, more optimized system. Claims and friends will no longer be saved in configs even if this is false");
		save();
	}
	
}
