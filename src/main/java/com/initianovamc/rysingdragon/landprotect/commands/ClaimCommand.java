package com.initianovamc.rysingdragon.landprotect.commands;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.LandProtect;
import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
import com.initianovamc.rysingdragon.landprotect.utils.ClaimBoundary;
import com.initianovamc.rysingdragon.landprotect.utils.ClaimKey;
import com.initianovamc.rysingdragon.landprotect.utils.PlayerClaim;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import org.spongepowered.api.Sponge;
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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
						int claimAmount;
						try {
							claimAmount = LandProtectDB.getPlayerClaimAmount(player.getUniqueId());
							int bonus = Integer.parseInt(optSubject.getOption("bonusclaims").orElse("0"));
							if (claimLimit != 0) {
								if (claimAmount + bonus >= claimLimit) {
									player.sendMessage(Text.of(TextColors.RED, "You have reached the max claim limit"));
									return CommandResult.success();
								} 
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						 
					} 
				} 
				
				LandProtectDB.addPlayerClaim(new PlayerClaim(worldUUID, chunk, player.getUniqueId()));
				player.sendMessage(Text.of(TextColors.DARK_AQUA, "You have claimed this chunk"));
				
				ClaimBoundary boundary = new ClaimBoundary(player, chunk);
				boundary.spawnTimedResetDelay(TimeUnit.SECONDS, 60);
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
