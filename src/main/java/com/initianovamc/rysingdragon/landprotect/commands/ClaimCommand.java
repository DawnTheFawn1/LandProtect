package com.initianovamc.rysingdragon.landprotect.commands;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.option.OptionSubject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.UUID;

public class ClaimCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			Vector3i chunk = player.getLocation().getChunkPosition();
			UUID worldUUID = player.getWorld().getUniqueId();
			
			if (!Utils.claimingEnabled(worldUUID)) {
				player.sendMessage(Text.of(TextColors.RED, "Claiming is not enabled in this world"));
				return CommandResult.success();
			}
			
			if (!Utils.isClaimed(chunk, worldUUID)) {				
				
				if (player instanceof Subject) {
					Subject subject = player.getContainingCollection().get(player.getIdentifier());
					if (subject instanceof OptionSubject) {
						OptionSubject optSubject = (OptionSubject)subject;
						int claimLimit = Integer.parseInt(optSubject.getOption("claimlimit").orElse("0"));
						int claims = Utils.getOwnedClaims(player.getUniqueId(), worldUUID).size();
						
						if (claimLimit != 0) {
							if (claims >= claimLimit) {
								player.sendMessage(Text.of(TextColors.RED, "You have reached the max claim limit"));
								return CommandResult.success();
							} 
						}
						 
					} 
				} 
				
				List<Vector3i> claims = Utils.getOwnedClaims(player.getUniqueId(), worldUUID);
				claims.add(chunk);
				Utils.setClaims(player.getUniqueId(), worldUUID, claims, "owned");
				
				player.sendMessage(Text.of(TextColors.DARK_AQUA, "You have claimed this chunk"));
				
			} else {
				player.sendMessage(Text.of(TextColors.RED, "This land is already claimed"));
			}
			return CommandResult.success();
			
		} else {
			src.sendMessage(Text.of(TextColors.RED, "You must be a player to use this command"));
			return CommandResult.empty();
		}
	}

}
