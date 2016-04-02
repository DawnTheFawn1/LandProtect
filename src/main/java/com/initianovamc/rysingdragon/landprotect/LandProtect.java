package com.initianovamc.rysingdragon.landprotect;

import com.google.inject.Inject;
import com.initianovamc.rysingdragon.landprotect.commands.CommandRegistry;
import com.initianovamc.rysingdragon.landprotect.config.ClaimConfig;
import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import com.initianovamc.rysingdragon.landprotect.config.PlayerConfig;
import com.initianovamc.rysingdragon.landprotect.listeners.ChangeBlockListener;
import com.initianovamc.rysingdragon.landprotect.listeners.InteractBlockListener;
import com.initianovamc.rysingdragon.landprotect.listeners.LoadWorldListener;
import com.initianovamc.rysingdragon.landprotect.listeners.PlayerJoinListener;
import com.initianovamc.rysingdragon.landprotect.listeners.PlayerMoveListener;
import me.flibio.updatifier.Updatifier;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Updatifier(repoName = "LandProtect", repoOwner = "RysingDragon", version = LandProtect.PLUGIN_VERSION)
@Plugin(name = LandProtect.PLUGIN_NAME, id = LandProtect.PLUGIN_ID, version = LandProtect.PLUGIN_VERSION, description = LandProtect.PLUGIN_DESCRIPTION, authors = LandProtect.PLUGIN_AUTHOR, dependencies = @Dependency(id = "Updatifier", optional = true))
public class LandProtect {

	public static final String PLUGIN_ID = "com.initianovamc.rysingdragon.landprotect";
	public static final String PLUGIN_VERSION = "v2.2.1-BETA";
	public static final String PLUGIN_DESCRIPTION = "A land protection plugin";
	public static final String PLUGIN_NAME = "LandProtect";
	public static final String PLUGIN_AUTHOR = "RysingDragon";
	public static LandProtect instance;
	
	@Inject
	private Logger logger;
	
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	
	@Inject
	@DefaultConfig(sharedRoot = false)
	private File defaultConfig;
	
	@Listener
	public void onPreInit(GamePreInitializationEvent event) {
		instance = this;
		logger.info("Setting up config");
		if (!Files.exists(getConfigDir())) {
			try {
				Files.createDirectory(getConfigDir());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ClaimConfig.getClaimConfig().setup();
		PlayerConfig.getPlayerConfig().setup();
		GeneralConfig.getConfig().setup();
	}	
	
	@Listener
	public void onInit(GameInitializationEvent event) {
		logger.info("Registering commands and event listeners");
		CommandRegistry.registerCommands();
		Sponge.getGame().getEventManager().registerListeners(this, new ChangeBlockListener());
		Sponge.getGame().getEventManager().registerListeners(this, new InteractBlockListener());
		Sponge.getGame().getEventManager().registerListeners(this, new PlayerJoinListener());
		Sponge.getGame().getEventManager().registerListeners(this, new PlayerMoveListener());
		Sponge.getGame().getEventManager().registerListeners(this, new LoadWorldListener());
	}
	
	@Listener
	public void onPostInit(GamePostInitializationEvent event) {
		
	}
	
	public Path getConfigDir() {
		return configDir;
	}
	
	public File getConfigFile() {
		return defaultConfig;
	}
	
	public Logger getLogger() {
		return logger;
	}
	
}