package com.initianovamc.rysingdragon.landprotect.commands;

import com.flowpowered.math.vector.Vector3i;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TrustCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			User trustedPlayer = (User)args.getOne("player").get();
			Vector3i chunk = player.getLocation().getChunkPosition();
			UUID worldUUID = player.getWorld().getUniqueId();
			
			if (Utils.isClaimed(chunk, worldUUID)) {
				if (Utils.getClaimOwner(chunk, worldUUID).isPresent()) {
					if (player.getUniqueId().equals(Utils.getClaimOwner(chunk, worldUUID).get())) {		
						
						ClaimKey key = new ClaimKey(worldUUID, chunk);
						List<UUID> list = new ArrayList<>();
						if (LandProtectDB.trustedPlayers.containsKey(key)) {
							list = LandProtectDB.trustedPlayers.get(key);
						}
						
						if (!list.contains(trustedPlayer.getUniqueId())) {
							list.add(trustedPlayer.getUniqueId());
							LandProtectDB.trustedPlayers.replace(key, list);
							LandProtectDB.addTrust(trustedPlayer.getUniqueId(), worldUUID, chunk);
							player.sendMessage(Text.of(TextColors.DARK_AQUA, "You have granted ", TextColors.GOLD, trustedPlayer.getName(), TextColors.DARK_AQUA, " access to this claim"));
						} else player.sendMessage(Text.of(TextColors.GOLD, trustedPlayer.getName(), TextColors.DARK_AQUA, " already has access to this claim"));
					} else player.sendMessage(Text.of(TextColors.RED, "You are not the owner of this claim"));
				} else 	player.sendMessage(Text.of(TextColors.RED, "This is Protected land"));
			} else 	player.sendMessage(Text.of(TextColors.RED, "This land is not claimed"));		
			
		} else {
			src.sendMessage(Text.of("You must be player to use this command"));
			return CommandResult.empty();
		}
		
		return CommandResult.success();
	}
}
