package com.initianovamc.rysingdragon.landprotect.commands;

import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.config.PlayerConfig;
import com.initianovamc.rysingdragon.landprotect.utils.FriendRequest;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AcceptFriendRequestCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player)src;
			
			Iterator<FriendRequest> iterator = Utils.friendRequests.iterator();
			
			while (iterator.hasNext()) {
				FriendRequest request = iterator.next();
				if (player.getUniqueId().equals(request.getFriend())) {
					UserStorageService service = Sponge.getServiceManager().provide(UserStorageService.class).get();
					if (service.get(request.getRequester()).isPresent()) {
						User user = service.get(request.getRequester()).get();
						try {
							List<String> playerFriendList = new ArrayList<>(PlayerConfig.getPlayerConfig().getConfigNode().getNode("friends", request.getRequester().toString(), "friendlist").getList(TypeToken.of(String.class)));
							List<String> friendList = new ArrayList<>(PlayerConfig.getPlayerConfig().getConfigNode().getNode("friends", request.getFriend().toString(), "friendlist").getList(TypeToken.of(String.class)));
							
							if (!playerFriendList.contains(request.getFriend().toString()) && !friendList.contains(request.getRequester().toString())) {
								playerFriendList.add(request.getFriend().toString());
								friendList.add(request.getRequester().toString());
								
								PlayerConfig.getPlayerConfig().getConfigNode().getNode("friends", request.getRequester().toString(), "friendlist").setValue(playerFriendList);
								PlayerConfig.getPlayerConfig().getConfigNode().getNode("friends", request.getFriend().toString(), "friendlist").setValue(friendList);
								PlayerConfig.getPlayerConfig().save();
								player.sendMessage(Text.of("You and ", user.getName(), " are now friends"));
								user.getPlayer().get().sendMessage(Text.of("You and ", player.getName(), " are now friends"));
								iterator.remove();
							}
							
						} catch (ObjectMappingException e) {
							e.printStackTrace();
						}
						
					}
				}
			}
		}
		
		return CommandResult.success();
	}
	
}
