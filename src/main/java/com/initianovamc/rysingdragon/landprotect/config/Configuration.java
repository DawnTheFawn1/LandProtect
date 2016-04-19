package com.initianovamc.rysingdragon.landprotect.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public interface Configuration {

	public void setup();
	
	public void save();
	
	public void load();
	
	public void setDefaults();
	
	public CommentedConfigurationNode getConfigNode();
	
}
