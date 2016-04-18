package com.initianovamc.rysingdragon.landprotect.commands;

import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class ReloadConfigCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		GeneralConfig.getConfig().load();
		return CommandResult.success();
	}

}
