package com.initianovamc.rysingdragon.landprotect.commands;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.config.ClaimConfig;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.UUID;

public class RemoveClaimCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			Vector3i chunk = player.getLocation().getChunkPosition();
			UUID worldUUID = player.getWorld().getUniqueId();
			
			if (Utils.isClaimed(chunk, worldUUID)) {
				if (Utils.getClaimOwner(chunk, worldUUID).isPresent()) {
					UUID owner = Utils.getClaimOwner(chunk, worldUUID).get();
					List<Vector3i> claims = Utils.getOwnedClaims(owner, worldUUID);	
					claims.remove(chunk);
					
					TypeToken<List<Vector3i>> token = new TypeToken<List<Vector3i>>() {};
					try {
						ClaimConfig.getClaimConfig().getConfigNode().getNode("claims", owner.toString(), "OwnedClaims").setValue(token, claims);
						player.sendMessage(Text.of("You have successfully removed this claim"));
					} catch (ObjectMappingException e) {
						e.printStackTrace();
					}
					
					if (Utils.getTrustedPlayers(chunk, worldUUID).isPresent()) {
						
						List<UUID> trustedPlayers = Utils.getTrustedPlayers(chunk, worldUUID).get();	
						for (UUID trusted : trustedPlayers) {
							
							List<Vector3i> trustedClaims = Utils.getOwnedClaims(trusted, worldUUID);
							trustedClaims.remove(chunk);
							try {
								ClaimConfig.getClaimConfig().getConfigNode().getNode("claims", trusted.toString(), "TrustedClaims").setValue(token, trustedClaims);
							} catch (ObjectMappingException e) {
								e.printStackTrace();
							}
						}
						
					}
					
				} else {
					player.sendMessage(Text.of("This land is not claimed by a player, use /lp unprotect to remove protected land"));
				}
			} else {
				player.sendMessage(Text.of("This land is not claimed"));
			}
		}
		
		return CommandResult.success();
	}

}
