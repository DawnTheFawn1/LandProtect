package com.initianovamc.rysingdragon.landprotect.listeners;

import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.config.PlayerConfig;
import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerJoinListener {

	@Listener
	public void onJoin(ClientConnectionEvent.Join event) {
		Player player = event.getTargetEntity();
		try {
			LandProtectDB.addPlayer(player.getUniqueId());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*List<String> registeredPlayers = new ArrayList<>();
		try {
			registeredPlayers = PlayerConfig.getPlayerConfig().getConfigNode().getNode("registeredPlayers").getList(TypeToken.of(String.class));
			List<String> registered = new ArrayList<>(registeredPlayers);
			if (!registered.contains(player.getUniqueId().toString())) {
				registered.add(player.getUniqueId().toString());
				PlayerConfig.getPlayerConfig().getConfigNode().getNode("registeredPlayers").setValue(registered);
				PlayerConfig.getPlayerConfig().save();
			}
		} catch (ObjectMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
}
