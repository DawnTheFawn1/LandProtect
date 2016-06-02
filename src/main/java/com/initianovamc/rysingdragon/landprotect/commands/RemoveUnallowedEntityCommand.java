package com.initianovamc.rysingdragon.landprotect.commands;

import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.config.WorldConfig;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

public class RemoveUnallowedEntityCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (src instanceof Player) {
			Player player = (Player) src;
			World world = player.getWorld();
			String entityId = (String) args.getOne("entity-id").get(); 
			if (!Sponge.getRegistry().getType(EntityType.class, entityId).isPresent()) {
				player.sendMessage(Text.of("That is an invalid entity id"));
				return CommandResult.empty();
			}
			
			try {
				List<String> unallowedEntities = new ArrayList<>(WorldConfig.getConfig().getConfigNode().getNode("Worlds", world.getUniqueId().toString(), "Riding", "UnallowedEntities").getList(TypeToken.of(String.class)));
				if (!unallowedEntities.contains(entityId)) {
					player.sendMessage(Text.of("That entity has not yet been added"));
					return CommandResult.empty();
				} else {
					unallowedEntities.remove(entityId);
					WorldConfig.getConfig().getConfigNode().getNode("Worlds", world.getUniqueId().toString(), "Riding", "UnallowedEntities").setValue(unallowedEntities);
					WorldConfig.getConfig().save();
					player.sendMessage(Text.of("You have successfully removed ", entityId, " as an unallowed entity in this world"));
					return CommandResult.success();
				}
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			}
			
		} else {
			src.sendMessage(Text.of("You must be a player to use this command"));
			return CommandResult.empty();
		}
		
		return CommandResult.empty();
	}

}
