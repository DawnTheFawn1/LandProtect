package com.initianovamc.rysingdragon.landprotect.listeners;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.World;

public class InteractEntityListener {

	@Listener
	public void onInteract(InteractEntityEvent event, @First Player player) {
		if (event.getTargetEntity() instanceof Hanging) {
			World world = player.getWorld();
			Vector3i chunk = player.getLocation().getChunkPosition();
			if (Utils.isAdminClaimed(chunk, world.getUniqueId()) && !player.hasPermission("landprotect.adminclaim.bypass")) {
				event.setCancelled(true);
			} else if (Utils.isPlayerClaimed(chunk, world.getUniqueId())) {
				if (!Utils.getClaimOwner(chunk, world.getUniqueId()).get().equals(player.getUniqueId())) {
					if (!Utils.isFriend(Utils.getClaimOwner(chunk, world.getUniqueId()).get(), player.getUniqueId()) && !Utils.isTrustedToClaim(chunk, player.getUniqueId(), world.getUniqueId())) {
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
