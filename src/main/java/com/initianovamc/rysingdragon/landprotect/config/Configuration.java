package com.initianovamc.rysingdragon.landprotect.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public interface Configuration {

	void setup();
	
	void save();
	
	void load();
	
	void setDefaults();
	
	CommentedConfigurationNode getConfigNode();
	
}
