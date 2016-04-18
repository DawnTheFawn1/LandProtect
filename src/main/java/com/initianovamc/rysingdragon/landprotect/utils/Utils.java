package com.initianovamc.rysingdragon.landprotect.utils;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class Utils {
	
	public static List<FriendRequest> friendRequests = new ArrayList<>();
	public static List<UUID> inAddInteractMode = new ArrayList<>();
	public static List<UUID> inRemoveInteractMode = new ArrayList<>();
	public static Map<UUID, Map<Vector3i, ClaimBoundary>> playerBoundaries = new HashMap<>();

	public static boolean isClaimed(Vector3i chunk, UUID worldUUID) {
		
		if (isAdminClaimed(chunk, worldUUID) || isPlayerClaimed(chunk, worldUUID)) {
			return true;
		}
		
		return false;
		
	}

	public static boolean isAdminClaimed(Vector3i chunk, UUID worldUUID) {
		ClaimKey key = new ClaimKey(worldUUID, chunk);
		Map<ClaimKey, AdminClaim> claims = LandProtectDB.adminclaims;
		if (claims.containsKey(key)) {
			return true;
		}
		
		return false;	
	}
	
	public static boolean isPlayerClaimed(Vector3i chunk, UUID worldUUID) {
		ClaimKey key = new ClaimKey(worldUUID, chunk);
		Map<ClaimKey, PlayerClaim> claims = LandProtectDB.playerclaims;
		
		if (claims.containsKey(key)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isFriend(UUID playerUUID, UUID friendUUID) {
		if (LandProtectDB.friendList.containsKey(playerUUID) && LandProtectDB.friendList.get(playerUUID).contains(friendUUID)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isTrustedToClaim(Vector3i chunk, UUID playerUUID, UUID worldUUID) {
		
		if (isPlayerClaimed(chunk, worldUUID)) {
			ClaimKey key = new ClaimKey(worldUUID, chunk);
			if (LandProtectDB.trustedPlayers.containsKey(key) && LandProtectDB.trustedPlayers.get(key).contains(playerUUID)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Optional<UUID> getClaimOwner(Vector3i chunk, UUID worldUUID) {	
		if (isPlayerClaimed(chunk, worldUUID)) {
			ClaimKey key = new ClaimKey(worldUUID, chunk);
			return Optional.of(LandProtectDB.playerclaims.get(key).getPlayerUUID());
		}
		
		return Optional.empty();
	}

	public static boolean claimingEnabled(UUID worldUUID) {
		return GeneralConfig.getConfig().getConfigNode().getNode("Worlds", worldUUID.toString(), "ClaimingEnabled").getBoolean();
	}
	
	public static String getBoundaryBlock() {
		if (GeneralConfig.getConfig().getConfigNode().getNode("BoundaryBlock").getString() != null) {
			return GeneralConfig.getConfig().getConfigNode().getNode("BoundaryBlock").getString();
		} else {
			return "minecraft:coal_block";
		}
	}
	
	public static String getClaimInspectTool() {
		if (GeneralConfig.getConfig().getConfigNode().getNode("InspectTool").getString() != null) {
			return GeneralConfig.getConfig().getConfigNode().getNode("InspectTool").getString();
		} else {
			return "minecraft:wooden_axe";
		}
	}
	
}
