package com.initianovamc.rysingdragon.landprotect;

import com.google.inject.Inject;
import com.initianovamc.rysingdragon.landprotect.commands.CommandRegistry;
import com.initianovamc.rysingdragon.landprotect.config.ClaimConfig;
import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import com.initianovamc.rysingdragon.landprotect.config.PlayerConfig;
import com.initianovamc.rysingdragon.landprotect.config.WorldConfig;
import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
import com.initianovamc.rysingdragon.landprotect.listeners.ChangeBlockListener;
import com.initianovamc.rysingdragon.landprotect.listeners.InteractEntityListener;
import com.initianovamc.rysingdragon.landprotect.listeners.EntityMoveListener;
import com.initianovamc.rysingdragon.landprotect.listeners.InteractBlockListener;
import com.initianovamc.rysingdragon.landprotect.listeners.LoadWorldListener;
import com.initianovamc.rysingdragon.landprotect.listeners.PlayerMoveListener;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
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
import org.spongepowered.api.service.economy.EconomyService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Optional;

@Updatifier(repoName = "LandProtect", repoOwner = "RysingDragon", version = LandProtect.PLUGIN_VERSION)
@Plugin(name = LandProtect.PLUGIN_NAME, id = LandProtect.PLUGIN_ID, version = LandProtect.PLUGIN_VERSION, description = LandProtect.PLUGIN_DESCRIPTION, authors = LandProtect.PLUGIN_AUTHOR, dependencies = @Dependency(id = "Updatifier", optional = true))
public class LandProtect {

	public static final String PLUGIN_ID = "com.initianovamc.rysingdragon.landprotect";
	public static final String PLUGIN_VERSION = "v3.4.0";
	public static final String PLUGIN_DESCRIPTION = "A land protection plugin";
	public static final String PLUGIN_NAME = "LandProtect";
	public static final String PLUGIN_AUTHOR = "RysingDragon";
	public static LandProtect instance;
	public static EconomyService economy;
	
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
		GeneralConfig.getConfig().setup();
		WorldConfig.getConfig().setup();
		
		if (Utils.legacyTransferEnabled()) {
			try {
				PlayerConfig.getPlayerConfig().setup();
				ClaimConfig.getClaimConfig().setup();
				LandProtectDB.setup();
				Utils.transferLegacyData();
				LandProtectDB.read();
				GeneralConfig.getConfig().getConfigNode().getNode("DataTransfer").setValue(false);
				GeneralConfig.getConfig().save();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try {
				LandProtectDB.setup();
				LandProtectDB.read();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}	
	
	@Listener
	public void onInit(GameInitializationEvent event) {
		Optional<EconomyService> service = Sponge.getServiceManager().provide(EconomyService.class);
		if (service.isPresent()) {
			economy = service.get();
		}
		logger.info("Registering commands and event listeners");
		CommandRegistry.registerCommands();
		Sponge.getGame().getEventManager().registerListeners(this, new ChangeBlockListener());
		Sponge.getGame().getEventManager().registerListeners(this, new InteractBlockListener());
		Sponge.getGame().getEventManager().registerListeners(this, new PlayerMoveListener());
		Sponge.getGame().getEventManager().registerListeners(this, new LoadWorldListener());
		Sponge.getGame().getEventManager().registerListeners(this, new EntityMoveListener());
		Sponge.getGame().getEventManager().registerListeners(this, new InteractEntityListener());
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