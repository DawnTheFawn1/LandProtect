package com.initianovamc.rysingdragon.landprotect.commands;

import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class SetBoundaryBlockCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		String id = (String)args.getOne("block-id").get();
		if (Sponge.getRegistry().getType(BlockType.class, id).isPresent()) {
			GeneralConfig.getConfig().getConfigNode().getNode("BoundaryBlock").setValue(id);
			GeneralConfig.getConfig().save();
			src.sendMessage(Text.of(TextColors.GOLD, id, TextColors.DARK_AQUA, " has been successfully added as the claim boundary block"));
		} else {
			src.sendMessage(Text.of(TextColors.RED, "That is not a proper block id"));
		}
		
		return CommandResult.success();
	}
	
}
