package com.initianovamc.rysingdragon.landprotect.listeners;

import com.initianovamc.rysingdragon.landprotect.config.WorldConfig;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.world.World;

import java.util.ArrayList;

public class LoadWorldListener {

	@Listener
	public void onLoad(LoadWorldEvent event) {
		
		World world = event.getTargetWorld();
		CommentedConfigurationNode root = WorldConfig.getConfig().getConfigNode();
		
		if (root.getNode("Worlds", world.getUniqueId().toString()).isVirtual()) root.getNode("Worlds", world.getUniqueId().toString()).setComment("Name of this world is: " + world.getName());
		if (root.getNode("Worlds", world.getUniqueId().toString(), "ClaimingEnabled").getValue() == null) root.getNode("Worlds", world.getUniqueId().toString(), "ClaimingEnabled").setValue(true); 
		if (root.getNode("Worlds", world.getUniqueId().toString(), "Riding", "EnabledInPlayerClaims").getValue() == null) root.getNode("Worlds", world.getUniqueId().toString(), "Riding", "EnabledInPlayerClaims").setValue(true);
		if (root.getNode("Worlds", world.getUniqueId().toString(), "Riding", "EnabledInAdminClaims").getValue() == null) root.getNode("Worlds", world.getUniqueId().toString(), "Riding", "EnabledInAdminClaims").setValue(true);
		if (root.getNode("Worlds", world.getUniqueId().toString(), "Riding", "UnallowedEntities").getValue() == null) root.getNode("Worlds", world.getUniqueId().toString(), "Riding", "UnallowedEntities").setValue(new ArrayList<>());
		WorldConfig.getConfig().save();
	}
	
}
