package com.initianovamc.rysingdragon.landprotect.utils;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.config.ClaimConfig;
import com.initianovamc.rysingdragon.landprotect.config.PlayerConfig;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class Utils {
	
	public static List<FriendRequest> friendRequests = new ArrayList<>();

	public static boolean isClaimed(Vector3i chunk) {
		
		for (Vector3i claim : getProtectedClaims()) {
			if (claim.equals(chunk)) {
				return true;
			}
		}
		
		try {
			for (String uuid : PlayerConfig.getPlayerConfig().getConfigNode().getNode("registeredPlayers").getList(TypeToken.of(String.class))) {
				if (ClaimConfig.getClaimConfig().getConfigNode().getNode("claims", uuid, "OwnedClaims").getList(TypeToken.of(Vector3i.class)).contains(chunk)) {
					return true;
				}
			}
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}

	public static boolean isProtected(Vector3i chunk) {
		
		for (Vector3i claim : getProtectedClaims()) {
			if (claim.equals(chunk)) {
				return true;
			}
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
			List<String> friendList = PlayerConfig.getPlayerConfig().getConfigNode().getNode("friends", playerUUID, "friendlist").getList(TypeToken.of(String.class));
			return friendList;
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public static boolean isTrustedToClaim(Vector3i chunk, UUID player) {
		
		if (isClaimed(chunk)) {
			try {
				List<Vector3i> claims = ClaimConfig.getClaimConfig().getConfigNode().getNode("claims", player.toString(), "TrustedClaims").getList(TypeToken.of(Vector3i.class));
				if (claims.contains(chunk)) {
					return true;
				}
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public static Optional<UUID> getClaimOwner(Vector3i chunk) {
		
		if (isClaimed(chunk)) {
			
			try {
				for (String uuid : PlayerConfig.getPlayerConfig().getConfigNode().getNode("registeredPlayers").getList(TypeToken.of(String.class))) {
					if (ClaimConfig.getClaimConfig().getConfigNode().getNode("claims", uuid, "OwnedClaims").getList(TypeToken.of(Vector3i.class)).contains(chunk)) {
						return Optional.of(UUID.fromString(uuid));
					}
				}
				
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			}
		}
		
		return Optional.empty();
	}
	
	public static List<Vector3i> getProtectedClaims() {
		
		List<Vector3i> protectedClaims = new ArrayList<>();
		try {
			protectedClaims = new ArrayList<>(ClaimConfig.getClaimConfig().getConfigNode().getNode("claims", "Protected").getList(TypeToken.of(Vector3i.class), new ArrayList<>()));
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		
		return protectedClaims;
	}
	
	public static List<Vector3i> getClaims(UUID player) {
		
		try {
			List<Vector3i> claims = new ArrayList<>(ClaimConfig.getClaimConfig().getConfigNode().getNode("claims", player.toString(), "OwnedClaims").getList(TypeToken.of(Vector3i.class)));
			return claims;
		
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}	
		return new ArrayList<>();
	}

	public static Optional<List<UUID>> getTrustedPlayers(Vector3i chunk) {
		
		try {
			List<String> uuidList = PlayerConfig.getPlayerConfig().getConfigNode().getNode("registeredPlayers").getList(TypeToken.of(String.class));
			List<UUID> trustedPlayers = new ArrayList<>();
			for (String uuidString : uuidList) {
				List<Vector3i> trustedClaims = ClaimConfig.getClaimConfig().getConfigNode().getNode("claims", uuidString, "TrustedClaims").getList(TypeToken.of(Vector3i.class));
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
	
}
