package com.initianovamc.rysingdragon.landprotect.commands;

import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
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

public class RemoveFriendCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			User user = (User)args.getOne("friend").get();
			if (Utils.isFriend(player.getUniqueId(), user.getUniqueId())) {
				LandProtectDB.removeFriend(player.getUniqueId(), user.getUniqueId());
				player.sendMessage(Text.of(TextColors.DARK_AQUA, "You and ", TextColors.GOLD, user.getName(), TextColors.DARK_AQUA, " are no longer friends"));
				if (user.getPlayer().isPresent()) {
					user.getPlayer().get().sendMessage(Text.of(TextColors.GOLD, player.getName(), TextColors.DARK_AQUA, " has deleted you as a friend"));
				}
			} else player.sendMessage(Text.of(TextColors.DARK_AQUA, "You are not friends with that player"));
		}
		
		return CommandResult.success();
	}

}
