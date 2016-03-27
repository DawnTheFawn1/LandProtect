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

import java.util.ArrayList;
import java.util.List;

public class UntrustCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			Player trusted = (Player)args.getOne("player").get();
			Vector3i chunk = player.getLocation().getChunkPosition();
			
			if (Utils.isClaimed(chunk)) {
				if (Utils.getClaimOwner(chunk).isPresent()) {
					if (player.getUniqueId().equals(Utils.getClaimOwner(chunk).get())) {
						
						try {
							TypeToken<List<Vector3i>> token = new TypeToken<List<Vector3i>>() {};
							List<Vector3i> trustedClaims = new ArrayList<>(ClaimConfig.getClaimConfig().getConfigNode().getNode("claims", trusted.getUniqueId().toString(), "TrustedClaims").getList(TypeToken.of(Vector3i.class)));
						
							if (trustedClaims.contains(chunk)) {
								trustedClaims.remove(chunk);
								ClaimConfig.getClaimConfig().getConfigNode().getNode("claims", trusted.getUniqueId().toString(), "TrustedClaims").setValue(token, trustedClaims);
								ClaimConfig.getClaimConfig().save();
								player.sendMessage(Text.of("You have removed ", trusted.getName(), " from access to this claim"));
							} else {
								player.sendMessage(Text.of(trusted.getName(), " does not have access to this claim"));
							}
							
						} catch (ObjectMappingException e) {
							e.printStackTrace();
						}
					} else {
						player.sendMessage(Text.of("You do not own this claim"));
					}
					
				} else {
					player.sendMessage(Text.of("You may not give trust to a protected claim"));
				}	
				
			} else {
				player.sendMessage(Text.of("This land is not claimed"));
			}
		}
		
		return CommandResult.success();
	}

}
