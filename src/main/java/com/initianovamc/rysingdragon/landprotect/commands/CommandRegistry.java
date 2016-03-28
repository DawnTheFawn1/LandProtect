package com.initianovamc.rysingdragon.landprotect.commands;

import com.initianovamc.rysingdragon.landprotect.LandProtect;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistry {
	
	private static LandProtect plugin = LandProtect.instance;

	public static void registerCommands() {
		
		CommandSpec landProtect = CommandSpec.builder()
				.children(getSubCommands())
				.description(Text.of("base command for all LandProtect commands"))
				.executor(new LandProtectCommand())
				.build();
		Sponge.getGame().getCommandManager().register(plugin, landProtect, "landprotect", "lp");
		
	}
	
	public static Map<List<String>, CommandSpec> getSubCommands() {
		
		Map<List<String>, CommandSpec> commands = new HashMap<>();
		
		CommandSpec addInteractable = CommandSpec.builder()
				.description(Text.of("Adds a block as interactable in protected zones"))
				.permission("landprotect.addinteractable")
				.executor(new AddInteractable())
				.arguments(GenericArguments.optional(GenericArguments.string(Text.of("block-id"))))
				.build();
		commands.put(Arrays.asList("addinteractable"), addInteractable);
		
		CommandSpec claim = CommandSpec.builder()
				.description(Text.of("Claims the chunk the player is standing in when used"))
				.permission("landprotect.claim.command")
				.executor(new ClaimCommand())
				.build();
		commands.put(Arrays.asList("claim"), claim);
		
		CommandSpec addFriend = CommandSpec.builder()
				.description(Text.of("Sends a request to a player so both of you will have access to each other's claims"))
				.permission("landprotect.addfriend")
				.executor(new AddFriendCommand())
				.arguments(GenericArguments.player(Text.of("friend")))
				.build();
		commands.put(Arrays.asList("addfriend"), addFriend);
		
		CommandSpec acceptFriend = CommandSpec.builder()
				.description(Text.of("Accepts a player's friend request"))
				.permission("landprotect.acceptfriend")
				.executor(new AcceptFriendRequestCommand())
				.build();
		commands.put(Arrays.asList("acceptrequest"), acceptFriend);
		
		CommandSpec protect = CommandSpec.builder()
				.description(Text.of("Claims the chunk you're standing in as a protected area"))
				.permission("landprotect.protect.command")
				.executor(new ProtectCommand())
				.build();
		commands.put(Arrays.asList("protect"), protect);
		
		CommandSpec trust = CommandSpec.builder()
				.description(Text.of("Adds the player access to your claim you're standing in"))
				.permission("landprotect.trust")
				.executor(new TrustCommand())
				.arguments(GenericArguments.player(Text.of("player")))
				.build();
		commands.put(Arrays.asList("trust"), trust);
		
		CommandSpec unclaim = CommandSpec.builder()
				.description(Text.of("Unclaims the chunk you're standing in and removes all trusted people access to it"))
				.permission("landprotect.unclaim")
				.executor(new UnclaimCommand())
				.build();
		commands.put(Arrays.asList("unclaim"), unclaim);
		
		CommandSpec unprotect = CommandSpec.builder()
				.description(Text.of("Removes this chunk as a protected area"))
				.permission("landprotect.unprotect")
				.executor(new UnprotectCommand())
				.build();
		commands.put(Arrays.asList("unprotect"), unprotect);
		
		CommandSpec help = CommandSpec.builder()
				.description(Text.of("help command"))
				.permission("landprotect.help")
				.executor(new HelpCommand())
				.build();
		commands.put(Arrays.asList("help"), help);
		
		CommandSpec removeClaim = CommandSpec.builder()
				.description(Text.of("forcefully removes a claimed chunk"))
				.permission("landprotect.removeclaim")
				.executor(new RemoveClaimCommand())
				.build();
		commands.put(Arrays.asList("removeclaim"), removeClaim);
		
		CommandSpec removeFriend = CommandSpec.builder()
				.description(Text.of("removes the targeted player as a friend"))
				.permission("landprotect.removefriend")
				.executor(new RemoveFriendCommand())
				.arguments(GenericArguments.player(Text.of("friend")))
				.build();
		commands.put(Arrays.asList("removefriend"), removeFriend);
		
		CommandSpec untrust = CommandSpec.builder()
				.description(Text.of("Remove a player as trusted from your land"))
				.permission("landprotect.untrust")
				.executor(new UntrustCommand())
				.arguments(GenericArguments.player(Text.of("player")))
				.build();
		commands.put(Arrays.asList("untrust"), untrust);
		
		return commands;
		
	}
	
}
