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
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.UUID;

public class ProtectCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			Vector3i chunk = player.getLocation().getChunkPosition();
			UUID worldUUID = player.getWorld().getUniqueId();
			
			if (!Utils.isClaimed(chunk, player.getWorld().getUniqueId())) {
				List<Vector3i> protectedList = Utils.getProtectedClaims(worldUUID);
				protectedList.add(chunk);
				Utils.setProtectedClaims(worldUUID, protectedList);
				player.sendMessage(Text.of(TextColors.RED, "You have claimed this chunk"));
				
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
