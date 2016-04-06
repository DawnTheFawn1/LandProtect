package com.initianovamc.rysingdragon.landprotect.commands;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.UUID;

public class UntrustCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			User trusted = (Player)args.getOne("player").get();
			Vector3i chunk = player.getLocation().getChunkPosition();
			UUID worldUUID = player.getWorld().getUniqueId();
			
			if (Utils.isClaimed(chunk, worldUUID)) {
				if (Utils.getClaimOwner(chunk, worldUUID).isPresent()) {
					if (player.getUniqueId().equals(Utils.getClaimOwner(chunk, worldUUID).get())) {
						
						TypeToken<List<Vector3i>> token = new TypeToken<List<Vector3i>>() {};
						List<Vector3i> trustedClaims = Utils.getTrustedClaims(trusted.getUniqueId(), worldUUID);

						if (trustedClaims.contains(chunk)) {
							trustedClaims.remove(chunk);
							Utils.setClaims(trusted.getUniqueId(), worldUUID, trustedClaims, "trusted");
							player.sendMessage(Text.of(TextColors.DARK_AQUA, "You have removed ", TextColors.GOLD, trusted.getName(), TextColors.DARK_AQUA, " from access to this claim"));
						} else {
							player.sendMessage(Text.of(TextColors.GOLD, trusted.getName(), TextColors.DARK_AQUA, " does not have access to this claim"));
						}
					} else {
						player.sendMessage(Text.of(TextColors.RED, "You do not own this claim"));
					}
					
				} else {
					player.sendMessage(Text.of(TextColors.RED, "You may not give trust to a protected claim"));
				}	
				
			} else {
				player.sendMessage(Text.of(TextColors.RED, "This land is not claimed"));
			}
		}
		
		return CommandResult.success();
	}

}
