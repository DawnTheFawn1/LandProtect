package com.initianovamc.rysingdragon.landprotect.commands;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
import com.initianovamc.rysingdragon.landprotect.utils.AdminClaim;
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

public class UnprotectCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			Vector3i chunk = player.getLocation().getChunkPosition();
			UUID worldUUID = player.getWorld().getUniqueId();
			
			if (Utils.isAdminClaimed(chunk, worldUUID)) {
				LandProtectDB.removeAdminClaim(new AdminClaim(worldUUID, chunk));
				player.sendMessage(Text.of("you have unprotected this land."));
			} else {
				player.sendMessage(Text.of(TextColors.RED, "This land is not protected"));
			}
			return CommandResult.success();
			
		} else {
			src.sendMessage(Text.of(TextColors.RED, "You must be a player to use this command"));
			return CommandResult.empty();
		}	
	}
}
