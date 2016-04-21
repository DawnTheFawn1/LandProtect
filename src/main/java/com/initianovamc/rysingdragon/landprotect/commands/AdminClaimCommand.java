package com.initianovamc.rysingdragon.landprotect.commands;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.LandProtect;
import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
import com.initianovamc.rysingdragon.landprotect.utils.AdminClaim;
import com.initianovamc.rysingdragon.landprotect.utils.ClaimBoundary;
import com.initianovamc.rysingdragon.landprotect.utils.ClaimKey;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AdminClaimCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			Vector3i chunk = player.getLocation().getChunkPosition();
			UUID worldUUID = player.getWorld().getUniqueId();
			
			if (!Utils.isClaimed(chunk, worldUUID)) {
				LandProtectDB.addAdminClaim(new AdminClaim(worldUUID, chunk));
				ClaimBoundary boundary = new ClaimBoundary(player, chunk);
				boundary.spawn();
				ClaimKey key = new ClaimKey(worldUUID, chunk);
				if (!Utils.claimBoundaries.containsKey(key)) {
					Utils.claimBoundaries.put(key, boundary);
				}
				Sponge.getScheduler().createTaskBuilder().execute(()-> {
					boundary.reset(); 
					Utils.claimBoundaries.remove(key);
					}).delay(60, TimeUnit.SECONDS).submit(LandProtect.instance);
				
				player.sendMessage(Text.of(TextColors.DARK_AQUA, "You have claimed this chunk as adminclaim"));
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
