package com.initianovamc.rysingdragon.landprotect.listeners;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.UUID;

public class PlayerMoveListener {

	@Listener
	public void onPlayerMove(DisplaceEntityEvent.TargetPlayer event) {
		Player player = event.getTargetEntity();
		Vector3i oldChunk = event.getFromTransform().getLocation().getChunkPosition();
		Vector3i newChunk = event.getToTransform().getLocation().getChunkPosition();
		UUID worldUUID = player.getWorld().getUniqueId();
		
		if (!oldChunk.equals(newChunk)) {
			
			if (Utils.isClaimed(oldChunk, worldUUID) && !Utils.isClaimed(newChunk, worldUUID)) {
				player.sendMessage(Text.of(TextColors.DARK_AQUA, "Now entering unclaimed land"));
				
			} else if (!Utils.isClaimed(oldChunk, worldUUID) && Utils.isClaimed(newChunk, worldUUID)) {
				
				if (Utils.getClaimOwner(newChunk, worldUUID).isPresent()) {
					UUID ownerUUID = Utils.getClaimOwner(newChunk, worldUUID).get();
					UserStorageService service = Sponge.getGame().getServiceManager().provide(UserStorageService.class).get();
					User owner = service.get(ownerUUID).get();
					player.sendMessage(Text.of(TextColors.DARK_AQUA, "Now entering the land of ", TextColors.GOLD, owner.getName()));
				} else {
					player.sendMessage(Text.of(TextColors.DARK_AQUA, "Now entering ", TextColors.GOLD, "protected ", TextColors.DARK_AQUA, "land"));
				}
				
			} else if (Utils.isClaimed(oldChunk, worldUUID) && Utils.isClaimed(newChunk, worldUUID)) {
				
				if (Utils.isProtected(oldChunk, worldUUID) && Utils.isProtected(newChunk, worldUUID)) {
					return;
					
				} else if (Utils.getClaimOwner(oldChunk, worldUUID).isPresent() && Utils.isProtected(newChunk, worldUUID)) {
					player.sendMessage(Text.of(TextColors.DARK_AQUA, "Now entering ", TextColors.GOLD, "protected ", TextColors.DARK_AQUA, "land"));
					
				} else if (Utils.getClaimOwner(newChunk, worldUUID).isPresent()) {
					
					if (Utils.getClaimOwner(oldChunk, worldUUID).isPresent()) {
						if (Utils.getClaimOwner(oldChunk, worldUUID).get().equals(Utils.getClaimOwner(newChunk, worldUUID).get())) {
							return;
						}
					}
					
					UUID ownerUUID = Utils.getClaimOwner(newChunk, worldUUID).get();
					UserStorageService service = Sponge.getGame().getServiceManager().provide(UserStorageService.class).get();
					User owner = service.get(ownerUUID).get();
					player.sendMessage(Text.of(TextColors.DARK_AQUA, "Now entering the land of ", TextColors.GOLD, owner.getName()));
				}
				
			}
		}
		
	}
	
}
