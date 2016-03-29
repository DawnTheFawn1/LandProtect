package com.initianovamc.rysingdragon.landprotect.commands;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.config.ClaimConfig;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;

public class ProtectCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			Vector3i chunk = player.getLocation().getChunkPosition();
			if (!Utils.isClaimed(chunk)) {
				List<Vector3i> protectedList = Utils.getProtectedClaims();
				protectedList.add(chunk);
				Utils.setProtectedClaims(protectedList);
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
