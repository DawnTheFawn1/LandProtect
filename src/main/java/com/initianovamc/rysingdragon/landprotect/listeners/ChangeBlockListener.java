package com.initianovamc.rysingdragon.landprotect.listeners;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.UUID;

public class ChangeBlockListener {

	@Listener
	public void onBreak(ChangeBlockEvent.Break event) {
		Vector3i chunk = event.getTransactions().get(0).getFinal().getLocation().get().getChunkPosition();
		UUID worldUUID = event.getTargetWorld().getUniqueId();
		UUID playerUUID;
		
		if (Utils.isClaimed(chunk, worldUUID)) {
			
			if (event.getCause().first(User.class).isPresent()) {
				User user = event.getCause().first(User.class).get();
				playerUUID = user.getUniqueId();
				
				if (Utils.getClaimOwner(chunk, worldUUID).isPresent()) {
					if (user.hasPermission("landprotect.claim.bypass")) {
						return;
					}
				} else {
					if (user.hasPermission("landprotect.protect.bypass")) {
						return;
					}
				}
				
			} else if (event.getCause().first(Player.class).isPresent()) {
				Player player = event.getCause().first(Player.class).get();
				playerUUID = player.getUniqueId();
				
				if (Utils.getClaimOwner(chunk, worldUUID).isPresent()) {
					if (player.hasPermission("landprotect.claim.bypass")) {
						return;
					}
				} else {
					if (player.hasPermission("landprotect.protect.bypass")) {
						return;
					}
				}
				
			} else {
				event.setCancelled(true);
				return;
			}
			
			if (Utils.getClaimOwner(chunk, worldUUID).isPresent()) {
				UUID owner = Utils.getClaimOwner(chunk, worldUUID).get();
				if (owner.equals(playerUUID)) {
					return;
				}
				
				if (Utils.isFriend(playerUUID, owner)) {
					return;
				}
				
				if (Utils.isTrustedToClaim(chunk, playerUUID, worldUUID)) {
					return;
				}
				
			}
			
			
			event.setCancelled(true);
		}
	}
	
	@Listener
	public void onPlace(ChangeBlockEvent.Place event) {
		Vector3i chunk = event.getTransactions().get(0).getFinal().getLocation().get().getChunkPosition();
		UUID worldUUID = event.getTargetWorld().getUniqueId();
		UUID playerUUID;
		
		if (Utils.isClaimed(chunk, worldUUID)) {
			
			if (event.getCause().first(User.class).isPresent()) {
				User user = event.getCause().first(User.class).get();
				playerUUID = user.getUniqueId();
				
				if (Utils.getClaimOwner(chunk, worldUUID).isPresent()) {
					if (user.hasPermission("landprotect.claim.bypass")) {
						return;
					}
				} else {
					if (user.hasPermission("landprotect.protect.bypass")) {
						return;
					}
				}
				
			} else if (event.getCause().first(Player.class).isPresent()) {
				Player player = event.getCause().first(Player.class).get();
				playerUUID = player.getUniqueId();
				
				if (Utils.getClaimOwner(chunk, worldUUID).isPresent()) {
					if (player.hasPermission("landprotect.claim.bypass")) {
						return;
					}
				} else {
					if (player.hasPermission("landprotect.protect.bypass")) {
						return;
					}
				}
				
			} else {
				event.setCancelled(true);
				return;
			}
			
			if (Utils.getClaimOwner(chunk, worldUUID).isPresent()) {
				UUID owner = Utils.getClaimOwner(chunk, worldUUID).get();
				if (owner.equals(playerUUID)) {
					return;
				}
				
				if (Utils.isFriend(playerUUID, owner)) {
					return;
				}
				
				if (Utils.isTrustedToClaim(chunk, playerUUID, worldUUID)) {
					return;
				}
				
			}
			
			event.setCancelled(true);
		}
	}
	
}
