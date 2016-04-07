package com.initianovamc.rysingdragon.landprotect.utils;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.config.ClaimConfig;
import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import com.initianovamc.rysingdragon.landprotect.config.PlayerConfig;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class Utils {
	
	public static List<FriendRequest> friendRequests = new ArrayList<>();
	public static List<UUID> inAddInteractMode = new ArrayList<>();
	public static List<UUID> inRemoveInteractMode = new ArrayList<>();

	public static boolean isClaimed(Vector3i chunk, UUID worldUUID) {
		
		for (Vector3i claim : getProtectedClaims(worldUUID)) {
			if (claim.equals(chunk)) {
				return true;
			}
		}
		
		try {
			for (String uuid : PlayerConfig.getPlayerConfig().getConfigNode().getNode("registeredPlayers").getList(TypeToken.of(String.class))) {
				if (ClaimConfig.getClaimConfig().getConfigNode().getNode("PlayerClaims", uuid, "Worlds", worldUUID.toString(), "OwnedClaims").getList(TypeToken.of(Vector3i.class)).contains(chunk)) {
					return true;
				}
			}
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}

	public static boolean isProtected(Vector3i chunk, UUID worldUUID) {
		
		for (Vector3i claim : getProtectedClaims(worldUUID)) {
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
		
		if (isClaimed(chunk, worldUUID)) {
			
			try {
				for (String uuid : PlayerConfig.getPlayerConfig().getConfigNode().getNode("registeredPlayers").getList(TypeToken.of(String.class))) {
					if (ClaimConfig.getClaimConfig().getConfigNode().getNode("PlayerClaims", uuid, "Worlds", worldUUID.toString(), "OwnedClaims").getList(TypeToken.of(Vector3i.class)).contains(chunk)) {
						return Optional.of(UUID.fromString(uuid));
					}
				}
				
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			}
		}
		
		return Optional.empty();
	}
	
	public static List<Vector3i> getProtectedClaims(UUID worldUUID) {
		
		List<Vector3i> protectedClaims = new ArrayList<>();
		try {
			protectedClaims = new ArrayList<>(ClaimConfig.getClaimConfig().getConfigNode().getNode("ProtectedClaims", "Worlds", worldUUID.toString(), "Protected").getList(TypeToken.of(Vector3i.class), new ArrayList<>()));
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		
		return protectedClaims;
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

	public static void setProtectedClaims(UUID worldUUID, List<Vector3i> claimsList) {
		
		TypeToken<List<Vector3i>> token = new TypeToken<List<Vector3i>>() {};
		try {
			ClaimConfig.getClaimConfig().getConfigNode().getNode("ProtectedClaims", "Worlds", worldUUID.toString(), "Protected").setValue(token, claimsList);
			ClaimConfig.getClaimConfig().save();
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		
	}

	public static boolean claimingEnabled(UUID worldUUID) {
		return GeneralConfig.getConfig().getConfigNode().getNode("Worlds", worldUUID.toString(), "ClaimingEnabled").getBoolean();
	}
	
	public static double getYCoordinate(Location<World> location) {
		//TODO implement for getting the max y coordinate of the top surface block.
		return 0;
	}
	
}
