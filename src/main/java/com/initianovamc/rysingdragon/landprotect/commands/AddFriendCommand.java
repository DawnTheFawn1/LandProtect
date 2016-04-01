package com.initianovamc.rysingdragon.landprotect.commands;

import com.initianovamc.rysingdragon.landprotect.LandProtect;
import com.initianovamc.rysingdragon.landprotect.utils.FriendRequest;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class AddFriendCommand implements CommandExecutor{

	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			Player friend = (Player) args.getOne("friend").get();
			
			for (FriendRequest request : Utils.friendRequests) {
				if (request.getFriend().equals(friend.getUniqueId())) {
					player.sendMessage(Text.of(TextColors.RED, "You already have a pending invite to ", TextColors.GOLD, friend.getName()));
					return CommandResult.success();
				}
				if (request.getRequester().equals(friend.getUniqueId())) {
					player.sendMessage(Text.of(TextColors.RED, "You already have a pending invitation from ", TextColors.GOLD, friend.getName()));
					return CommandResult.success();
				}
			}
			
			player.sendMessage(Text.of(TextColors.DARK_AQUA, "You have sent a friend request to ", TextColors.GOLD, friend.getName(), TextColors.DARK_AQUA, ", They have 60 seconds to accept"));
			friend.sendMessage(Text.of(TextColors.DARK_AQUA, "You have a pending friend request from ", TextColors.GOLD, player.getName(), TextColors.DARK_AQUA, "You have 60 seconds to accept"));
			
			Utils.friendRequests.add(new FriendRequest(player.getUniqueId(), friend.getUniqueId()));
			
			Scheduler scheduler = Sponge.getGame().getScheduler();
			Task.Builder taskBuilder = scheduler.createTaskBuilder();
			Task task = taskBuilder.execute(new CancellingTask(friend.getUniqueId())).interval(1, TimeUnit.SECONDS).name("LandProtect Friend Request Task").submit(LandProtect.instance);
		}
		
		return CommandResult.success();
	}

	private class CancellingTask implements Consumer<Task> {
		private UUID friend;
		public CancellingTask(UUID friend) {
			this.friend = friend;
		}
		
		private int timeLeft = 60;
		@Override
		public void accept(Task t) {
			--timeLeft;
			if (timeLeft < 1) {
				Utils.friendRequests.remove(friend);
				t.cancel();
			}	
		}
	}
	
}

