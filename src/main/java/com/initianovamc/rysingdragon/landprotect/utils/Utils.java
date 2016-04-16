package com.initianovamc.rysingdragon.landprotect.utils;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.config.ClaimConfig;
import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import com.initianovamc.rysingdragon.landprotect.config.PlayerConfig;
import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;

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
		
		/*try {
			for (String uuid : PlayerConfig.getPlayerConfig().getConfigNode().getNode("registeredPlayers").getList(TypeToken.of(String.class))) {
				if (ClaimConfig.getClaimConfig().getConfigNode().getNode("PlayerClaims", uuid, "Worlds", worldUUID.toString(), "OwnedClaims").getList(TypeToken.of(Vector3i.class)).contains(chunk)) {
					return true;
				}
			}
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}*/
		
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
		
		List<String> friendList = getFriendList(playerUUID);
		
		if (friendList.contains(friendUUID.toString())) {
			return true;
		}
		return false;
	}
	
	public static List<String> getFriendList(UUID playerUUID) {
		
		try {
			List<String> friendList = PlayerConfig.getPlayerConfig().getConfigNode().getNode("friends", playerUUID.toString(), "friendlist").getList(TypeToken.of(String.class));
			return friendList;
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public static boolean isTrustedToClaim(Vector3i chunk, UUID player, UUID worldUUID) {
		
		if (isClaimed(chunk, worldUUID)) {
			try {
				List<Vector3i> claims = ClaimConfig.getClaimConfig().getConfigNode().getNode("PlayerClaims", player.toString(), "Worlds", worldUUID.toString(), "TrustedClaims").getList(TypeToken.of(Vector3i.class));
				if (claims.contains(chunk)) {
					return true;
				}
			} catch (ObjectMappingException e) {
				e.printStackTrace();
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
	
	public static List<Vector3i> getOwnedClaims(UUID playerUUID, UUID worldUUID) {
		try {
			List<Vector3i> claims = new ArrayList<>(ClaimConfig.getClaimConfig().getConfigNode().getNode("PlayerClaims", playerUUID.toString(), "Worlds", worldUUID.toString(), "OwnedClaims").getList(TypeToken.of(Vector3i.class)));
			return claims;
		
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}	
		return new ArrayList<>();
	}

	public static List<Vector3i> getTrustedClaims(UUID playerUUID, UUID worldUUID) {
		
		try {
			List<Vector3i> trustedList = new ArrayList<>(ClaimConfig.getClaimConfig().getConfigNode().getNode("PlayerClaims", playerUUID.toString(), "Worlds", worldUUID.toString(), "TrustedClaims").getList(TypeToken.of(Vector3i.class)));
			return trustedList;
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();
		
	}
	
	public static Optional<List<UUID>> getTrustedPlayers(Vector3i chunk, UUID worldUUID) {
		
		try {
			List<String> uuidList = PlayerConfig.getPlayerConfig().getConfigNode().getNode("registeredPlayers").getList(TypeToken.of(String.class));
			List<UUID> trustedPlayers = new ArrayList<>();
			for (String uuidString : uuidList) {
				List<Vector3i> trustedClaims = Utils.getTrustedClaims(UUID.fromString(uuidString), worldUUID);
				if (trustedClaims.contains(chunk)) {
					trustedPlayers.add(UUID.fromString(uuidString));
				}
			}
			return Optional.of(trustedPlayers);
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		
		return Optional.empty();
	}
	
	public static void setClaims(UUID playerUUID, UUID worldUUID, List<Vector3i> claimsList, String claimType) {
		
		TypeToken<List<Vector3i>> token = new TypeToken<List<Vector3i>>() {};

		switch(claimType) {
		
		case "owned":
			try {
				ClaimConfig.getClaimConfig().getConfigNode().getNode("PlayerClaims", playerUUID.toString(), "Worlds", worldUUID.toString(), "OwnedClaims").setValue(token, claimsList);
				ClaimConfig.getClaimConfig().save();
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			}
			break;
		case "trusted":
			try {
				ClaimConfig.getClaimConfig().getConfigNode().getNode("PlayerClaims", playerUUID.toString(), "Worlds", worldUUID.toString(), "TrustedClaims").setValue(token, claimsList);
				ClaimConfig.getClaimConfig().save();
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			}
			break;
		
		}
		
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
