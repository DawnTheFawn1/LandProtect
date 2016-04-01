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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InteractBlockListener {

	@Listener
	public void onInteract(InteractBlockEvent.Secondary event, @First Player player) {
		
		if (event.getCause().containsType(Player.class)) {
			
			UUID worldUUID = player.getWorld().getUniqueId();
			
			if (Utils.inInteractMode.contains(player.getUniqueId())) {
				Utils.inInteractMode.remove(player.getUniqueId());
				event.setCancelled(true);
				try {
					List<String> interactables = GeneralConfig.getConfig().getConfigNode().getNode("Interactable").getList(TypeToken.of(String.class), new ArrayList<>());
					interactables.add(event.getTargetBlock().getState().getType().getName());
					GeneralConfig.getConfig().getConfigNode().getNode("Interactable").setValue(interactables);
					GeneralConfig.getConfig().save();
					player.sendMessage(Text.of("id ", event.getTargetBlock().getState().getType().getName(), " has been added to the list of interactable blocks"));
				} catch (ObjectMappingException e) {
					e.printStackTrace();
				}
			}
			
			if (event.getTargetBlock().getLocation().isPresent()) {
				Vector3i chunk = event.getTargetBlock().getLocation().get().getChunkPosition();
				
				if (Utils.isClaimed(chunk, worldUUID)) {
				
					if (Utils.isProtected(chunk, worldUUID)) {
						
						if (player.hasPermission("landprotect.protect.bypass")) {
							return;
						}
						
						try {
							List<String> interactables = GeneralConfig.getConfig().getConfigNode().getNode("Interactable").getList(TypeToken.of(String.class), new ArrayList<>());
							if (!interactables.contains(event.getTargetBlock().getState().getType().getName())) {
								event.setCancelled(true);
							}
						} catch (ObjectMappingException e) {
							e.printStackTrace();
						}
						
					}
					
					if (Utils.isTrustedToClaim(chunk, player.getUniqueId(), worldUUID)) {
						return;
					}
					
					if (Utils.getClaimOwner(chunk, worldUUID).isPresent()) {
						UUID owner = Utils.getClaimOwner(chunk, worldUUID).get();
						if (owner.equals(player.getUniqueId())) {
							return;
						} else if (Utils.isFriend(owner, player.getUniqueId())) {
							return;
						}
					}
					
					if (player.hasPermission("landprotect.claim.bypass")) {
						return;
					}	
					event.setCancelled(true);
					player.sendMessage(Text.of(TextColors.RED, "This land is claimed"));
				}
			}
		}
	}
}
