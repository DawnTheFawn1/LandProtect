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

public class UnclaimCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			Vector3i chunk = player.getLocation().getChunkPosition();
			
			if (Utils.isClaimed(chunk)) {
				
				if (Utils.getClaimOwner(chunk).isPresent()) {	
					UUID owner = Utils.getClaimOwner(chunk).get();
					
					if (player.getUniqueId().equals(owner)) {
						List<Vector3i> claims = Utils.getClaims(player.getUniqueId());
						TypeToken<List<Vector3i>> token = new TypeToken<List<Vector3i>>() {};
						if (claims.contains(chunk)) {
							claims.remove(chunk);
							try {
								ClaimConfig.getClaimConfig().getConfigNode().getNode("claims", player.getUniqueId().toString(), "OwnedClaims").setValue(token, claims);
							} catch (ObjectMappingException e) {
								e.printStackTrace();
							}
						} else {
							player.sendMessage(Text.of("You do not own this claim"));
						}
						if (Utils.getTrustedPlayers(chunk).isPresent()) {
							List<UUID> trustedPlayers = Utils.getTrustedPlayers(chunk).get();
							for (UUID trusted : trustedPlayers) {
								
								List<Vector3i> trustedClaims = Utils.getClaims(trusted);
								trustedClaims.remove(chunk);
								try {
									ClaimConfig.getClaimConfig().getConfigNode().getNode("claims", trusted.toString(), "TrustedClaims").setValue(token, trustedClaims);
								} catch (ObjectMappingException e) {
									e.printStackTrace();
								}
							}
						}		
						
					} else {
						player.sendMessage(Text.of("You do not own this claim"));
					}
					
				}
				
			} else {
				player.sendMessage(Text.of("This land is not claimed"));
			}
			
		} else {
			src.sendMessage(Text.of("You must be a player to use this command"));
		}
		
		return CommandResult.success();
	}

}
