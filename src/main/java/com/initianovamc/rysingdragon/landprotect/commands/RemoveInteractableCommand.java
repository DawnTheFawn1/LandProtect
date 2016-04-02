package com.initianovamc.rysingdragon.landprotect.commands;

import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class RemoveInteractableCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (args.hasAny("block-id")) {
			String id = (String)args.getOne("block-id").get();
			List<String> interactables = new ArrayList<>();
			if (!Sponge.getRegistry().getType(BlockType.class, id).isPresent()) {
				src.sendMessage(Text.of(TextColors.RED, "That is not a correct block id"));
				return CommandResult.success();
			}
			
			try {
				interactables = GeneralConfig.getConfig().getConfigNode().getNode("Interactable").getList(TypeToken.of(String.class), new ArrayList<>());
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			}
			
			if (interactables.contains(id)) {
				interactables.remove(id);
				GeneralConfig.getConfig().getConfigNode().getNode("Interactable").setValue(interactables);
				GeneralConfig.getConfig().save();
				src.sendMessage(Text.of(TextColors.DARK_AQUA, "id ", TextColors.GOLD, id, TextColors.DARK_AQUA, " has been removed as interactable"));
			} else {
				src.sendMessage(Text.of(TextColors.RED, "That block id is not yet added as interactable"));
				return CommandResult.success();
			}
		} else {
			if (src instanceof Player) {
				Player player = (Player)src;
				Utils.inRemoveInteractMode.add(player.getUniqueId());
				player.sendMessage(Text.of(TextColors.DARK_AQUA, "Right click a block to remove it as interactable"));
			} 
			
		}
		
		return CommandResult.success();
	}

}
