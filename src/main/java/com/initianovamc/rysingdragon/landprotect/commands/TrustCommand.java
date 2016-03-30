package com.initianovamc.rysingdragon.landprotect.commands;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.UUID;

public class TrustCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			Player trustedPlayer = (Player)args.getOne("player").get();
			Vector3i chunk = player.getLocation().getChunkPosition();
			UUID worldUUID = player.getWorld().getUniqueId();
			
			if (Utils.isClaimed(chunk, worldUUID)) {
				if (Utils.getClaimOwner(chunk, worldUUID).isPresent()) {
					if (player.getUniqueId().equals(Utils.getClaimOwner(chunk, worldUUID).get())) {		
						
						List<Vector3i> list = Utils.getTrustedClaims(trustedPlayer.getUniqueId(), worldUUID);
						if (!list.contains(chunk)) {
							list.add(chunk);
							Utils.setClaims(trustedPlayer.getUniqueId(), worldUUID, list, "trusted");
							player.sendMessage(Text.of("You have granted ", trustedPlayer.getName(), " access to this claim"));
						} else {
							player.sendMessage(Text.of(trustedPlayer.getName(), " already has access to this claim"));
						}
						
					} else {
						player.sendMessage(Text.of("You are not the owner of this claim"));
					}
					
				} else {
					player.sendMessage(Text.of("This is Protected land"));
				}
				
			} else {
				player.sendMessage(Text.of("This land is not claimed"));
			}
			
		}
		
		return CommandResult.success();
	}

}
