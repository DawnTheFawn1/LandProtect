package com.initianovamc.rysingdragon.landprotect.commands;

import com.initianovamc.rysingdragon.landprotect.LandProtect;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.option.OptionSubject;
import org.spongepowered.api.service.permission.option.OptionSubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;

public class BuyClaimsCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (src instanceof Player) {
			Player player = (Player) src;
			int claimsToBuy = (int) args.getOne("claims").get();
			
			if (claimsToBuy <= 0) {
				player.sendMessage(Text.of(TextColors.RED, "That is an invalid amount"));
				return CommandResult.success();
			}
			
			Subject subject = player.getContainingCollection().get(player.getIdentifier());
			SubjectData subjectData = player.getSubjectData();
			if (subject instanceof OptionSubject && subjectData instanceof OptionSubjectData) {
				OptionSubject optSubject = (OptionSubject) subject;
				OptionSubjectData optData = (OptionSubjectData) subjectData;
				
				if (LandProtect.economy != null && Utils.economyEnabled()) {
					BigDecimal currentAmount = LandProtect.economy.getOrCreateAccount(player.getUniqueId()).get().getBalance(LandProtect.economy.getDefaultCurrency());
					BigDecimal amountNeeded = new BigDecimal(claimsToBuy).multiply(Utils.getEconomyPrice());
					
					if (currentAmount.compareTo(amountNeeded) == -1) {
						player.sendMessage(Text.of(TextColors.RED, "You do not have enough money for that"));
						return CommandResult.success();
					} 
					int bonus = Integer.parseInt(optSubject.getOption("bonusclaims").orElse("0"));
					optData.setOption(optData.GLOBAL_CONTEXT, "bonusclaims", String.valueOf(claimsToBuy + bonus));
					LandProtect.economy.getOrCreateAccount(player.getUniqueId()).get().setBalance(LandProtect.economy.getDefaultCurrency(), currentAmount.subtract(amountNeeded), Cause.of(NamedCause.source(LandProtect.instance)));
					player.sendMessage(Text.of(TextColors.DARK_AQUA, "Successfully bought ", claimsToBuy, " claims"));
					return CommandResult.success();
					
				} else {
					int currentExp = player.get(Keys.TOTAL_EXPERIENCE).get();
					int expNeeded = claimsToBuy * Utils.getExpPrice();
					
					if (currentExp < expNeeded) {
						player.sendMessage(Text.of(TextColors.RED, "You do not have enough experience"));
						return CommandResult.success();
					}
					int bonus = Integer.parseInt(optSubject.getOption("bonusclaims").orElse("0"));
					optData.setOption(optData.GLOBAL_CONTEXT, "bonusclaims", String.valueOf(claimsToBuy + bonus));
					player.offer(Keys.EXPERIENCE_LEVEL, currentExp - expNeeded);
					player.sendMessage(Text.of(TextColors.DARK_AQUA, "Successfully bought ", claimsToBuy, " claims"));
					return CommandResult.success();
					
				} 
			} else {
				return CommandResult.success();
			}
			
		} else {
			src.sendMessage(Text.of("You must be a player to use this command"));
			return CommandResult.success();
		}	
	}
}
