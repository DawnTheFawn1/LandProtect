package com.initianovamc.rysingdragon.landprotect.commands;

import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;

public class ReloadDataCommand implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		src.sendMessage(Text.of("reloading data..."));
		if (LandProtectDB.sql == null || LandProtectDB.dataSource == null) {
			try {
				LandProtectDB.setup();
				LandProtectDB.read();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			LandProtectDB.adminclaims.clear();
			LandProtectDB.friendList.clear();
			LandProtectDB.playerclaims.clear();
			LandProtectDB.trustedPlayers.clear();
			try {
				LandProtectDB.read();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		src.sendMessage(Text.of("data successfully reloaded"));
		return CommandResult.success();
	}

}
