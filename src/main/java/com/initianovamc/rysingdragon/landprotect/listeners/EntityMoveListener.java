package com.initianovamc.rysingdragon.landprotect.listeners;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public class EntityMoveListener {

	@Listener
	public void onMove(DisplaceEntityEvent.Move event) {
		Vector3i oldChunk = event.getFromTransform().getLocation().getChunkPosition();
		Vector3i newChunk = event.getToTransform().getLocation().getChunkPosition();
		Entity entity = event.getTargetEntity();
		World world = event.getFromTransform().getExtent();
		
		if (entity.getPassenger().isPresent()) {
			if (entity.getPassenger().get() instanceof Player) {
				Player player = (Player) entity.getPassenger().get();
				if (!Utils.ridingEnabled(world.getUniqueId(), newChunk, entity.getType().getId())) {
					player.setVehicle(null);
					player.sendMessage(Text.of(TextColors.RED, "You are not allowed to ride that entity in this location"));
				} else {
					return;
				}
			}
		}	
	}
}
