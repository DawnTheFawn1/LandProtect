package com.initianovamc.rysingdragon.landprotect.config;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.LandProtect;
import com.initianovamc.rysingdragon.landprotect.utils.Vector3iSerializer;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import java.io.IOException;
import java.nio.file.Path;

public class ClaimConfig implements Configuration{

	private static ClaimConfig claimConfig = new ClaimConfig();
	private Path configFile = LandProtect.instance.getConfigDir().resolve("claims.conf");
	private ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();
	private CommentedConfigurationNode configNode;
	
	private TypeToken<Vector3i> token = new TypeToken<Vector3i>() {};
	private TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild().registerType(token, new Vector3iSerializer());
	private ConfigurationOptions options = ConfigurationOptions.defaults().setSerializers(serializers);
	
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
	
	public void save() {
		try {
			configLoader.save(configNode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load() {
		try {
			configNode = configLoader.load(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setDefaults() {
		configNode.getNode("claims").setComment("All claims will be stored here.");
	}
	
	public static ClaimConfig getClaimConfig() {
		return claimConfig;
	}
	
	public CommentedConfigurationNode getConfigNode() {
		return configNode;
	}
	
}
