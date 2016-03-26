package com.initianovamc.rysingdragon.landprotect.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			
		PaginationList.Builder paginationBuilder = PaginationList.builder();
		List<Text> helpText = new ArrayList<>();
		
		for (List<String> aliases : CommandRegistry.getSubCommands().keySet()) {
			CommandSpec spec = CommandRegistry.getSubCommands().get(aliases);
			Text helpCommand = Text.builder()
					.append(Text.of(TextColors.DARK_AQUA, "Command: ", TextColors.GOLD, aliases.toString(), "\n"))
					.append(Text.of(TextColors.DARK_AQUA, "Info: ", TextColors.GOLD, spec.getShortDescription(src).get(), "\n"))
					.append(Text.of(TextColors.DARK_AQUA, "Usage: ", TextColors.GOLD, "/", aliases.get(0), " ", spec.getUsage(src), "\n"))
					.append(Text.of(TextColors.DARK_AQUA, "Permission: ", TextColors.GOLD, spec.toString().substring(spec.toString().lastIndexOf("permission") +11, spec.toString().indexOf("argumentParser") -2), "\n")).color(TextColors.DARK_AQUA)
					.append(Text.of(TextColors.DARK_AQUA, "Access: ", TextColors.GOLD, spec.testPermission(src), " \n")).color(TextColors.DARK_AQUA)
					.build();
			helpText.add(helpCommand);
		}
		
		paginationBuilder.title(Text.of("LandProtect Help Menu")).contents(helpText).padding(Text.of("-")).linesPerPage(10).sendTo(src);
		return CommandResult.success();
	}
	
}
