package com.initianovamc.rysingdragon.landprotect.commands;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
import com.initianovamc.rysingdragon.landprotect.utils.ClaimKey;
import com.initianovamc.rysingdragon.landprotect.utils.PlayerClaim;
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
			User trusted = (User)args.getOne("player").get();
			Vector3i chunk = player.getLocation().getChunkPosition();
			UUID worldUUID = player.getWorld().getUniqueId();
			
			if (Utils.isClaimed(chunk, worldUUID)) {
				if (Utils.getClaimOwner(chunk, worldUUID).isPresent() && Utils.getClaimOwner(chunk, worldUUID).get().equals(player.getUniqueId())) {
					ClaimKey key = new ClaimKey(worldUUID, chunk);
					if (LandProtectDB.trustedPlayers.containsKey(key) && LandProtectDB.trustedPlayers.get(key).contains(trusted.getUniqueId())) {
						LandProtectDB.removeTrust(trusted.getUniqueId(), worldUUID, chunk);
						player.sendMessage(Text.of(TextColors.DARK_AQUA, "Successfully revoked ", TextColors.GOLD, trusted.getName(), TextColors.DARK_AQUA, " access to this claim"));
					} else player.sendMessage(Text.of(TextColors.GOLD, trusted.getName(), TextColors.DARK_AQUA, " does not have access to this claim"));
				} else player.sendMessage(Text.of(TextColors.RED, "You do not own this claim"));
			} else player.sendMessage(Text.of(TextColors.RED, "This land is not claimed"));
			
		} else {
			src.sendMessage(Text.of("You must be a player to use this command"));
			return CommandResult.empty();
		}
		return CommandResult.success();
	}
}
