package com.initianovamc.rysingdragon.landprotect.commands;

import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;

public class AddInteractable implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (src instanceof Player) {
			Player player = (Player)src;
			String id = (String)args.getOne("block-id").get();
			try {
				List<String> interactables = GeneralConfig.getConfig().getConfigNode().getNode("Interactable").getList(TypeToken.of(String.class), new ArrayList<>());
				interactables.add(id);
				GeneralConfig.getConfig().getConfigNode().getNode("Interactable").setValue(interactables);
				GeneralConfig.getConfig().save();
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			}
		}
		
		return CommandResult.success();
	}

}
