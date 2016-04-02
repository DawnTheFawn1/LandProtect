package com.initianovamc.rysingdragon.landprotect.commands;

import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class InteractableListCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException { 
		
		List<String> interactables = new ArrayList<>();
		
		try {
			interactables = GeneralConfig.getConfig().getConfigNode().getNode("Interactable").getList(TypeToken.of(String.class));
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		
		List<Text> contents = new ArrayList<>();
		for (String id : interactables) {
			contents.add(Text.of(TextColors.DARK_AQUA, id));
		}
		
		if (contents.size() == 0) {
			src.sendMessage(Text.of(TextColors.RED, "There are no blocks added as interactable"));
			return CommandResult.success();
		}
		
		PaginationList.Builder paginationBuilder = PaginationList.builder();
		paginationBuilder.title(Text.of(TextColors.GOLD, "Interactable Block IDs")).linesPerPage(10).contents(contents).padding(Text.of(TextColors.GOLD, "-")).sendTo(src);
		
		return CommandResult.success();
	}

}
