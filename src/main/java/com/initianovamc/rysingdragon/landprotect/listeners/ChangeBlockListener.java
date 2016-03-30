package com.initianovamc.rysingdragon.landprotect.listeners;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public class ChangeBlockListener {

	@Listener
	public void onBreak(ChangeBlockEvent.Break event, @Root Player player) {
		Vector3i chunk = player.getLocation().getChunkPosition();
		UUID worldUUID = player.getWorld().getUniqueId();
		if (Utils.isClaimed(chunk, worldUUID)) {
			if (Utils.getClaimOwner(chunk, worldUUID).isPresent()) {
				UUID owner = Utils.getClaimOwner(chunk, worldUUID).get();
				if (owner.equals(player.getUniqueId())) {
					return;
				}
				
				if (Utils.isFriend(player.getUniqueId(), owner)) {
					return;
				}
				
				if (Utils.isTrustedToClaim(chunk, player.getUniqueId(), worldUUID)) {
					return;
				}
				
				if (player.hasPermission("landprotect.claim.bypass")) {
					return;
				}
			}
			
			if (player.hasPermission("landprotect.protect.bypass")) {
				return;
			}
			
			event.setCancelled(true);
			player.sendMessage(Text.of("This land is claimed"));
		}
	}
	
	@Listener
	public void onPlace(ChangeBlockEvent.Place event, @Root Player player) {
		Vector3i chunk = player.getLocation().getChunkPosition();
		UUID worldUUID = player.getWorld().getUniqueId();
		
		if (Utils.isClaimed(chunk, worldUUID)) {
			if (Utils.getClaimOwner(chunk, worldUUID).isPresent()) {
				UUID owner = Utils.getClaimOwner(chunk, worldUUID).get();
				if (owner.equals(player.getUniqueId())) {
					return;
				}
				if (Utils.isFriend(player.getUniqueId(), owner)) {
					return;
				}
				
				if (Utils.isTrustedToClaim(chunk, player.getUniqueId(), worldUUID)) {
					return;
				}
				
				if (player.hasPermission("landprotect.claim.bypass")) {
					return;
				}
				
			}
			
			if (player.hasPermission("landprotect.protect.bypass")) {
				return;
			}
			
			event.setCancelled(true);
			player.sendMessage(Text.of("This land is claimed"));
		}
	}
	
}
