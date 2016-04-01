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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.UUID;

public class UnprotectCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			Vector3i chunk = player.getLocation().getChunkPosition();
			UUID worldUUID = player.getWorld().getUniqueId();
			
			if (Utils.isProtected(chunk, worldUUID)) {
				
				List<Vector3i> protectedClaims = Utils.getProtectedClaims(worldUUID);
				TypeToken<List<Vector3i>> token = new TypeToken<List<Vector3i>>() {};
				if (protectedClaims.contains(chunk)) {
					protectedClaims.remove(chunk);
					Utils.setProtectedClaims(worldUUID, protectedClaims);
					player.sendMessage(Text.of(TextColors.DARK_AQUA, "You have unprotected this land"));
				} 
				
			} else {
				player.sendMessage(Text.of(TextColors.RED, "This land is not protected"));
			}
			
		} else {
			src.sendMessage(Text.of(TextColors.RED, "You must be a player to use this command"));
		}
		
		return CommandResult.success();
	}

}
