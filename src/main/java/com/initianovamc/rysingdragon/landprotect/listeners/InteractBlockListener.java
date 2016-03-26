package com.initianovamc.rysingdragon.landprotect.listeners;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InteractBlockListener {

	@Listener
	public void onInteract(InteractBlockEvent.Secondary event, @First Player player) {
		
		if (event.getCause().containsType(Player.class)) {
			if (event.getTargetBlock().getLocation().isPresent()) {
				Vector3i chunk = event.getTargetBlock().getLocation().get().getChunkPosition();
				if (Utils.isProtected(chunk)) {
					try {
						List<String> interactables = GeneralConfig.getConfig().getConfigNode().getNode("Interactable").getList(TypeToken.of(String.class), new ArrayList<>());
						if (!interactables.contains(event.getTargetBlock().getState().getType().getName())) {
							event.setCancelled(true);
						}
					} catch (ObjectMappingException e) {
						e.printStackTrace();
					}
					
				}
				
				if (Utils.isClaimed(chunk)) {
					if (Utils.isTrustedToClaim(chunk, player.getUniqueId())) {
						return;
					}
					if (Utils.getClaimOwner(chunk).isPresent()) {
						UUID owner = Utils.getClaimOwner(chunk).get();
						if (owner.equals(player.getUniqueId())) {
							return;
						} else if (Utils.isFriend(owner, player.getUniqueId())) {
							return;
						}
					}
				}
			}
		}
	}
}
