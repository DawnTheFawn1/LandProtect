package com.initianovamc.rysingdragon.landprotect.commands;

import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
import com.initianovamc.rysingdragon.landprotect.utils.FriendRequest;
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

import java.util.Iterator;

public class AcceptFriendRequestCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			
			Iterator<FriendRequest> iterator = Utils.friendRequests.iterator();
			
			while (iterator.hasNext()) {
				FriendRequest request = iterator.next();
				if (player.getUniqueId().equals(request.getFriend())) {
					if (Sponge.getServer().getPlayer(request.getRequester()).isPresent()) {
						Player requester = Sponge.getServer().getPlayer(request.getRequester()).get();
						if (!Utils.isFriend(player.getUniqueId(), requester.getUniqueId())) {
							LandProtectDB.addFriend(player.getUniqueId(), requester.getUniqueId());
							LandProtectDB.addFriend(requester.getUniqueId(), player.getUniqueId());
							player.sendMessage(Text.of(TextColors.DARK_AQUA, "You and ", TextColors.GOLD, requester.getName(), TextColors.DARK_AQUA, " are now friends"));
							requester.sendMessage(Text.of(TextColors.DARK_AQUA, "You and ", TextColors.GOLD, player.getName(), TextColors.DARK_AQUA, " are now friends"));
							iterator.remove();
						}
					}
				}
			}
		}
		
		return CommandResult.success();
	}
	
}
