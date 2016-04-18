package com.initianovamc.rysingdragon.landprotect.commands;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
import com.initianovamc.rysingdragon.landprotect.utils.PlayerClaim;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.UUID;

public class UnclaimCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			Vector3i chunk = player.getLocation().getChunkPosition();
			UUID worldUUID = player.getWorld().getUniqueId();
			
			if (Utils.isPlayerClaimed(chunk, worldUUID)) {					
				UUID owner = Utils.getClaimOwner(chunk, worldUUID).get();
				if (player.getUniqueId().equals(owner)) {
					PlayerClaim claim = new PlayerClaim(worldUUID, chunk, player.getUniqueId());
					LandProtectDB.removePlayerClaim(claim);
					LandProtectDB.removeAllTrusts(claim);
					player.sendMessage(Text.of(TextColors.DARK_AQUA, "You have unclaimed this land"));		
					
				} else {
					player.sendMessage(Text.of(TextColors.RED, "You do not own this claim"));
				}
				
			} else {
				player.sendMessage(Text.of(TextColors.RED, "You do not own this land"));
			}
			
		} else {
			src.sendMessage(Text.of(TextColors.RED, "You must be a player to use this command"));
		}
		
		return CommandResult.success();
	}
}
