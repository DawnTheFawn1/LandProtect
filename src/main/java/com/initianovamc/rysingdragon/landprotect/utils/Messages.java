package com.initianovamc.rysingdragon.landprotect.utils;

import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import org.spongepowered.api.text.Text;

public class Messages {

	public static final String ADMINCLAIM_MESSAGE = GeneralConfig.getConfig().getConfigNode().getNode("Messages", "AdminClaimedMessage").getString();
	public static final String PLAYERCLAIM_MESSAGE = GeneralConfig.getConfig().getConfigNode().getNode("Messages", "PlayerClaimedMessage").getString();
	public static final String UNCLAIMED_MESSAGE = GeneralConfig.getConfig().getConfigNode().getNode("Messages", "UnclaimedMessage").getString();
	
}
