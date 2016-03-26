package com.initianovamc.rysingdragon.landprotect.commands;

import com.initianovamc.rysingdragon.landprotect.LandProtect;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class LandProtectCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		src.sendMessage(Text.of("This server is running ", LandProtect.PLUGIN_NAME, " ", LandProtect.PLUGIN_VERSION, ", made by ", LandProtect.PLUGIN_AUTHOR, "."));
		src.sendMessage(Text.of("If you wish to learn how to use ", LandProtect.PLUGIN_NAME, ", enter in /lp help"));
		return CommandResult.success();
	}

}
