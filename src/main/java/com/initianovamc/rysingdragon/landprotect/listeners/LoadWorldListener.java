package com.initianovamc.rysingdragon.landprotect.listeners;

import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.world.World;

public class LoadWorldListener {

	@Listener
	public void onLoad(LoadWorldEvent event) {
		
		World world = event.getTargetWorld();
		
		if (GeneralConfig.getConfig().getConfigNode().getNode("Worlds", world.getUniqueId().toString()).getValue() == null) {
			GeneralConfig.getConfig().getConfigNode().getNode("Worlds", world.getUniqueId().toString(), "ClaimingEnabled").setValue(true);
			GeneralConfig.getConfig().getConfigNode().getNode("Worlds", world.getUniqueId().toString(), "WorldName").setValue(world.getName());
			GeneralConfig.getConfig().save();
		}
		
	}
	
}
