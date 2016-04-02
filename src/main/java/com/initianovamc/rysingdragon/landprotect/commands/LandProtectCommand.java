package com.initianovamc.rysingdragon.landprotect.commands;

import com.initianovamc.rysingdragon.landprotect.LandProtect;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class LandProtectCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		src.sendMessage(Text.of(TextColors.DARK_AQUA, "This server is running ", TextColors.GOLD, LandProtect.PLUGIN_NAME, " ", LandProtect.PLUGIN_VERSION, ",", TextColors.DARK_AQUA, " made by ", TextColors.GOLD, LandProtect.PLUGIN_AUTHOR, "."));
		src.sendMessage(Text.of(TextColors.DARK_AQUA, "If you wish to learn how to use ", TextColors.GOLD, LandProtect.PLUGIN_NAME, TextColors.DARK_AQUA, ",", TextColors.DARK_AQUA, "enter in ", TextColors.GOLD, "/lp help"));
		return CommandResult.success();
	}

}
