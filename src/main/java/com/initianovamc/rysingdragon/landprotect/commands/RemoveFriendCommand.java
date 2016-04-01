package com.initianovamc.rysingdragon.landprotect.commands;

import com.initianovamc.rysingdragon.landprotect.config.PlayerConfig;
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

public class RemoveFriendCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			Player friend = (Player)args.getOne("friend").get();
			if (Utils.isFriend(player.getUniqueId(), friend.getUniqueId())) {
				List<String> playerList = Utils.getFriendList(player.getUniqueId());
				List<String> friendList = Utils.getFriendList(friend.getUniqueId());
				
				if (playerList.contains(friend.getUniqueId().toString()) && friendList.contains(player.getUniqueId().toString())) {
					playerList.remove(friend.getUniqueId().toString());
					friendList.remove(player.getUniqueId().toString());
					
					PlayerConfig.getPlayerConfig().getConfigNode().getNode("friends", player.getUniqueId().toString(), "friendlist").setValue(playerList);
					PlayerConfig.getPlayerConfig().getConfigNode().getNode("friends", friend.getUniqueId().toString(), "friendlist").setValue(friendList);
					PlayerConfig.getPlayerConfig().save();
					player.sendMessage(Text.of(TextColors.AQUA, "You and ", TextColors.GOLD, friend.getName(), TextColors.DARK_AQUA, " are no longer friends"));
					friend.sendMessage(Text.of(TextColors.GOLD, player.getName(), TextColors.DARK_AQUA, " has deleted you as a friend"));
				}
			}
		}
		
		return CommandResult.success();
	}

}
