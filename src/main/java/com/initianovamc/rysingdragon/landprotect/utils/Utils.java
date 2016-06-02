package com.initianovamc.rysingdragon.landprotect.utils;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.LandProtect;
import com.initianovamc.rysingdragon.landprotect.config.ClaimConfig;
import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import com.initianovamc.rysingdragon.landprotect.config.PlayerConfig;
import com.initianovamc.rysingdragon.landprotect.config.WorldConfig;
import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
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
	public static Map<ClaimKey, ClaimBoundary> claimBoundaries = new HashMap<>();

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
		return WorldConfig.getConfig().getConfigNode().getNode("Worlds", worldUUID.toString(), "ClaimingEnabled").getBoolean();
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
	
	public static boolean legacyTransferEnabled() {
		return GeneralConfig.getConfig().getConfigNode().getNode("DataTransfer").getBoolean();
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
	
	public static List<Vector3i> getTrustedClaims(UUID playerUUID, UUID worldUUID) {
		
		try {
			List<Vector3i> trustedList = new ArrayList<>(ClaimConfig.getClaimConfig().getConfigNode().getNode("PlayerClaims", playerUUID.toString(), "Worlds", worldUUID.toString(), "TrustedClaims").getList(TypeToken.of(Vector3i.class)));
			return trustedList;
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();
		
	}
	
	private static void transferTrustedPlayers() {
		try {
			LandProtect.instance.getLogger().info("transferring trusts");
			for (String playerUUID : PlayerConfig.getPlayerConfig().getConfigNode().getNode("registeredPlayers").getList(TypeToken.of(String.class))) {
				for (World world : Sponge.getServer().getWorlds()) {
					List<Vector3i> claims = getTrustedClaims(UUID.fromString(playerUUID), world.getUniqueId());
					for (Vector3i chunk : claims) {
						LandProtectDB.addTrust(UUID.fromString(playerUUID), world.getUniqueId(), chunk);
					}
				}
			}
			LandProtect.instance.getLogger().info("transferring trusts");
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
	}
	
	private static void transferPlayerData() {
		try {
			LandProtect.instance.getLogger().info("transferring friends");
			for (String playerUUID : PlayerConfig.getPlayerConfig().getConfigNode().getNode("registeredPlayers").getList(TypeToken.of(String.class))) {
				for (String uuid : getFriendList(UUID.fromString(playerUUID))) {
					LandProtectDB.addFriend(UUID.fromString(playerUUID), UUID.fromString(uuid));
				}
			}
			LandProtect.instance.getLogger().info("done transferring friends");
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
	}
	
	private static void transferAdminClaims() {
		try {
			LandProtect.instance.getLogger().info("transferring admin claims");
			for (World world : Sponge.getServer().getWorlds()) {
				for (Vector3i chunk : ClaimConfig.getClaimConfig().getConfigNode().getNode("ProtectedClaims", "Worlds", world.getUniqueId().toString(), "Protected").getList(TypeToken.of(Vector3i.class))) {
					AdminClaim claim = new AdminClaim(world.getUniqueId(), chunk);
					LandProtectDB.addAdminClaim(claim);
				}
			}
			LandProtect.instance.getLogger().info("done transferring admin claims");	
		} catch(ObjectMappingException e) {
			e.printStackTrace();
		}
	}
	
	private static void transferPlayerClaims() {
		try {
			LandProtect.instance.getLogger().info("transferring player claims");
			for (String playerUUID : PlayerConfig.getPlayerConfig().getConfigNode().getNode("registeredPlayers").getList(TypeToken.of(String.class))) {
				for (World world : Sponge.getServer().getWorlds()) {
					List<Vector3i> claims = ClaimConfig.getClaimConfig().getConfigNode().getNode("PlayerClaims", playerUUID, "Worlds", world.getUniqueId().toString() , "OwnedClaims").getList(TypeToken.of(Vector3i.class));
					for (Vector3i chunk : claims) {
						PlayerClaim claim = new PlayerClaim(world.getUniqueId(), chunk, UUID.fromString(playerUUID));
						LandProtectDB.addPlayerClaim(claim);
					}
				}
			}
			LandProtect.instance.getLogger().info("done transferring player claims");
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
	}
	
	public static void transferLegacyData() {
		transferPlayerData();
		transferAdminClaims();
		transferPlayerClaims();
		transferTrustedPlayers();
	}
	
	public static boolean economyEnabled() {
		return GeneralConfig.getConfig().getConfigNode().getNode("EconomyEnabled").getBoolean();
	}
	
	public static int getExpPrice() {
		return GeneralConfig.getConfig().getConfigNode().getNode("BonusClaims", "ExperiencePrice").getInt();
	}
	
	public static BigDecimal getEconomyPrice() {
		return new BigDecimal(GeneralConfig.getConfig().getConfigNode().getNode("BonusClaims", "EconomyPrice").getInt());
	}
	
	public static boolean adminClaimRidingEnabled(UUID worldUUID) {
		return WorldConfig.getConfig().getConfigNode().getNode("Worlds", worldUUID.toString(), "Riding", "EnabledInAdminClaims").getBoolean();
	}
	
	public static boolean playerClaimRidingEnabled(UUID worldUUID) {
		return WorldConfig.getConfig().getConfigNode().getNode("Worlds", worldUUID.toString(), "Riding", "EnabledInPlayerClaims").getBoolean();
	}
	
	public static boolean ridingEnabled(UUID worldUUID, Vector3i chunk, String entityID) {
		if (getUnallowedEntites(worldUUID).contains(entityID)) {
			if (!adminClaimRidingEnabled(worldUUID) && isAdminClaimed(chunk, worldUUID)) {
				return false;
			} else if (!playerClaimRidingEnabled(worldUUID) && isPlayerClaimed(chunk, worldUUID)) {
				return false;
			}
		} 
		return true;
	}
	
	public static List<String> getUnallowedEntites(UUID worldUUID) {
		try {
			return WorldConfig.getConfig().getConfigNode().getNode("Worlds", worldUUID.toString(), "Riding", "UnallowedEntities").getList(TypeToken.of(String.class));
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public static int getBonusClaimLimit() {
		return GeneralConfig.getConfig().getConfigNode().getNode("BonusClaims", "ClaimLimit").getInt();
	}
	
}
