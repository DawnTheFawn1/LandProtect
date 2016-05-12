package com.initianovamc.rysingdragon.landprotect.commands;

import com.initianovamc.rysingdragon.landprotect.LandProtect;
import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
import com.initianovamc.rysingdragon.landprotect.utils.PlayerClaim;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ListClaimsCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			
			Sponge.getScheduler().createTaskBuilder().async().execute(task -> {
				PaginationList.Builder paginationBuilder = PaginationList.builder();
				List<Text> contents = new ArrayList<>();
				
				for (PlayerClaim claim : LandProtectDB.playerclaims.values()) {
					if (claim.getPlayerUUID().equals(player.getUniqueId())) {
						World world = Sponge.getGame().getServer().getWorld(claim.getWorldUUID()).get();
						contents.add(Text.of(TextColors.DARK_AQUA, "World Name: ", TextColors.GOLD, world.getName(), TextColors.DARK_AQUA, " Chunk Coordinates: ", TextColors.GOLD, claim.getChunk()));
					}
				}
				PaginationList pagination = paginationBuilder.contents(contents).title(Text.of("Claims")).padding(Text.of("-")).linesPerPage(12).build();
				pagination.sendTo(player);
			}).submit(LandProtect.instance);
			
		}
		
		return CommandResult.success();
	}

}
