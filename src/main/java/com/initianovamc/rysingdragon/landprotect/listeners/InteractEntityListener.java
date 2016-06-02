package com.initianovamc.rysingdragon.landprotect.listeners;

import com.initianovamc.rysingdragon.landprotect.LandProtect;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;

public class InteractEntityListener {

	@Listener
	public void onInteract(InteractEntityEvent event) {
		if (event.getTargetEntity() instanceof Hanging) {
			event.setCancelled(true);
		}
	}
}
