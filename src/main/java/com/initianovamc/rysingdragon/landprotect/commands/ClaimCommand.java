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
						
						if (Utils.getClaims(player.getUniqueId()).isPresent()) {
							int claims = Utils.getClaims(player.getUniqueId()).get().size();
							if (claims >= claimLimit) {
								player.sendMessage(Text.of("You have reached the max claim limit"));
								return CommandResult.success();
							} else LandProtect.instance.getLogger().info("claim limit not reached");
						} else {
							LandProtect.instance.getLogger().info("claims not present");
						}
					} else LandProtect.instance.getLogger().info("not an option subject");

				} else LandProtect.instance.getLogger().info("not a subject");
				
				List<Vector3i> currentClaims = new ArrayList<>();
				try {
					currentClaims = ClaimConfig.getClaimConfig().getConfigNode().getNode("claims", player.getUniqueId().toString(), "OwnedClaims").getList(TypeToken.of(Vector3i.class));
				} catch (ObjectMappingException e) {
					e.printStackTrace();
				}
				TypeToken<List<Vector3i>> token = new TypeToken<List<Vector3i>>() {};
				List<Vector3i> claims = new ArrayList<>(currentClaims);
				claims.add(chunk);
				try {
					ClaimConfig.getClaimConfig().getConfigNode().getNode("claims", player.getUniqueId().toString(), "OwnedClaims").setValue(token, claims);
				} catch (ObjectMappingException e) {
					e.printStackTrace();
				}
				ClaimConfig.getClaimConfig().save();
				
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
