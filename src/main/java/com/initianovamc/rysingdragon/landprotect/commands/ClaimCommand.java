package com.initianovamc.rysingdragon.landprotect.commands;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.LandProtect;
import com.initianovamc.rysingdragon.landprotect.config.ClaimConfig;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.option.OptionSubject;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClaimCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			Vector3i chunk = player.getLocation().getChunkPosition();
			if (!Utils.isClaimed(chunk)) {				
				
				if (player instanceof Subject) {
					Subject subject = player.getContainingCollection().get(player.getIdentifier());
					if (subject instanceof OptionSubject) {
						OptionSubject optSubject = (OptionSubject)subject;
						int claimLimit = Integer.parseInt(optSubject.getOption("claimlimit").orElse("0"));
						int claims = Utils.getOwnedClaims(player.getUniqueId()).size();
						
						if (claimLimit != 0) {
							if (claims >= claimLimit) {
								player.sendMessage(Text.of("You have reached the max claim limit"));
								return CommandResult.success();
							} 
						}
						 
					} 
				} 
				
				List<Vector3i> claims = Utils.getOwnedClaims(player.getUniqueId());
				claims.add(chunk);
				Utils.setClaims(player.getUniqueId(), claims, "owned");
				
				player.sendMessage(Text.of("You have claimed this chunk"));
				
			} else {
				player.sendMessage(Text.of("This land is already claimed"));
			}
			return CommandResult.success();
			
		} else {
			src.sendMessage(Text.of("You must be a player to use this command"));
			return CommandResult.empty();
		}
	}

}
