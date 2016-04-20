package com.initianovamc.rysingdragon.landprotect.database;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.LandProtect;
import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import com.initianovamc.rysingdragon.landprotect.utils.AdminClaim;
import com.initianovamc.rysingdragon.landprotect.utils.ClaimKey;
import com.initianovamc.rysingdragon.landprotect.utils.PlayerClaim;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

public class LandProtectDB {

	private static SqlService sql;
	private static DataSource dataSource;
	public static Map<ClaimKey, PlayerClaim> playerclaims = new HashMap<>();
	public static Map<ClaimKey, AdminClaim> adminclaims = new HashMap<>();
	public static Map<ClaimKey, List<UUID>> trustedPlayers = new HashMap<>();
	public static Map<UUID, List<UUID>> friendList = new HashMap<>();
	
	public static DataSource getDataSource(String jdbcUrl) throws SQLException {
		return sql.getDataSource(jdbcUrl);
	}
	
	public static void setup() throws SQLException {
		
		sql = Sponge.getGame().getServiceManager().provide(SqlService.class).get();
		if (GeneralConfig.getConfig().getConfigNode().getNode("MySQL", "enabled").getBoolean()) {
			
			String database = GeneralConfig.getConfig().getConfigNode().getNode("MySQL", "database").getString();
			String host = GeneralConfig.getConfig().getConfigNode().getNode("MySQL", "host").getString();
			Integer port = GeneralConfig.getConfig().getConfigNode().getNode("MySQL", "port").getInt();
			String username = GeneralConfig.getConfig().getConfigNode().getNode("MySQL", "username").getString();
			String password = GeneralConfig.getConfig().getConfigNode().getNode("MySQL", "password").getString();
			
			dataSource = getDataSource("jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + username + "&password=" + password);
		} else {
			Path dataPath = LandProtect.instance.getConfigDir().resolve("data");
			
			if (!Files.exists(dataPath)) {
				try {
					Files.createDirectory(dataPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			dataSource = getDataSource("jdbc:sqlite:" + dataPath.toString() + "/LandProtect.db");
		}
		
		execute("CREATE TABLE IF NOT EXISTS adminclaims (worldUUID TEXT, chunkX INT, chunkZ INT)");
		execute("CREATE TABLE IF NOT EXISTS playerclaims (playerUUID TEXT, worldUUID TEXT, chunkX INT, chunkZ INT)");
		execute("CREATE TABLE IF NOT EXISTS friends (playerUUID TEXT, friendUUID TEXT)");
		execute("CREATE TABLE IF NOT EXISTS trustedplayers (playerUUID TEXT, worldUUID TEXT, chunkX INT, chunkZ INT)");
	}
	
	public static void read() throws SQLException {
		Connection conn = dataSource.getConnection();
		Statement statement = conn.createStatement();
		ResultSet rs = null;
		try {
			rs = statement.executeQuery("SELECT * FROM adminclaims");
				
			while (rs.next()) {
				UUID worldUUID = UUID.fromString(rs.getString("worldUUID"));
				Vector3i chunk = new Vector3i(rs.getInt("chunkX"), 0, rs.getInt("chunkZ"));
				AdminClaim adminclaim = new AdminClaim(worldUUID, chunk);
				ClaimKey key = new ClaimKey(worldUUID, chunk);
				adminclaims.put(key, adminclaim);
			}
				
		} finally {
			if (rs != null) rs.close();
		}
		
		try {
			rs = statement.executeQuery("SELECT * FROM playerclaims");
				
			while (rs.next()) {
				UUID playerUUID = UUID.fromString(rs.getString("playerUUID"));
				UUID worldUUID = UUID.fromString(rs.getString("worldUUID"));
				Vector3i chunk = new Vector3i(rs.getInt("chunkX"), 0, rs.getInt("chunkZ"));
				PlayerClaim adminclaim = new PlayerClaim(worldUUID, chunk, playerUUID);
				ClaimKey key = new ClaimKey(worldUUID, chunk);
				playerclaims.put(key, adminclaim);
			}
				
		} finally {
			if (rs != null) rs.close();
		}
		
		try {
			rs = statement.executeQuery("SELECT * FROM friends");
			
			while (rs.next()) {
				UUID playerUUID = UUID.fromString(rs.getString("playerUUID"));
				UUID friendUUID = UUID.fromString(rs.getString("friendUUID"));
				List<UUID> list = new ArrayList<>();
				if (friendList.containsKey(playerUUID)) {
					list = friendList.get(playerUUID);
				}
				list.add(friendUUID);
				friendList.put(playerUUID, list);
			}
		} finally {
			if (rs != null) rs.close();
		}
		
		try {
			rs = statement.executeQuery("SELECT * FROM trustedplayers");
			
			while (rs.next()) {
				UUID worldUUID = UUID.fromString(rs.getString("worldUUID"));
				Vector3i chunk = new Vector3i(rs.getInt("chunkX"), 0, rs.getInt("chunkZ"));
				ClaimKey key = new ClaimKey(worldUUID, chunk);
				UUID trustedPlayer = UUID.fromString(rs.getString("playerUUID"));
				List<UUID> list = new ArrayList<>();
				if (trustedPlayers.containsKey(key)) {
					list = trustedPlayers.get(key);
				}
				list.add(trustedPlayer);
				trustedPlayers.put(key, list);
			}
			 
		} finally {
			if (rs != null) rs.close();
			statement.close();
			conn.close();
		}
	}
	
	public static void execute(String execution) throws SQLException {
		Connection conn = dataSource.getConnection();
		Statement statement = conn.createStatement();
		
		statement.execute(execution);
		
		statement.close();
		conn.close();
	}
	
	public static void addPlayerClaim(PlayerClaim claim) {
		String execution = "INSERT INTO playerclaims (playerUUID, worldUUID, chunkX, chunkZ) VALUES ('" + claim.getPlayerUUID().toString() + "', '" + claim.getWorldUUID().toString() + "', '" + claim.getChunk().getX() + "', '" + claim.getChunk().getZ() + "')";
		try {
			execute(execution);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ClaimKey key = new ClaimKey(claim.getWorldUUID(), claim.getChunk());
		playerclaims.put(key, claim);
	}

	public static void removePlayerClaim(PlayerClaim claim) {
		String execution = "DELETE FROM playerclaims WHERE playerUUID = '" + claim.getPlayerUUID().toString() + "' AND worldUUID = '" + claim.getWorldUUID().toString() + "' AND chunkX = '" + claim.getChunk().getX() + "' AND chunkZ = '" + claim.getChunk().getZ() + "' ";
		try {
			execute(execution);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		ClaimKey key = new ClaimKey(claim.getWorldUUID(), claim.getChunk());
		playerclaims.remove(key);
	}
	
	public static int getPlayerClaimAmount(UUID playerUUID) throws SQLException {
		Connection conn = dataSource.getConnection();
		Statement statement = conn.createStatement();
		
		String execution = "SELECT COUNT(*) AS amount FROM playerclaims WHERE playerUUID = '" + playerUUID.toString() + "' ";
		ResultSet rs = statement.executeQuery(execution);
		int amount = 0;
		if (rs.next()) {
			amount = rs.getInt("amount");
		}
		
		rs.close();
		statement.close();
		conn.close();
		return amount;
		
	}
	
	public static void addAdminClaim(AdminClaim claim) {
		String execution = "INSERT INTO adminclaims (worldUUID, chunkX, chunkZ) VALUES ('" + claim.getWorldUUID().toString() + "', '" + claim.getChunk().getX() + "', '" + claim.getChunk().getZ() + "')";
		try {
			execute(execution);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		ClaimKey key = new ClaimKey(claim.getWorldUUID(), claim.getChunk());
		adminclaims.put(key, claim);
	}
	
	public static void removeAdminClaim(AdminClaim claim) {
		String execution = "DELETE FROM adminclaims WHERE worldUUID = '" + claim.getWorldUUID().toString() + "' AND chunkX = '" + claim.getChunk().getX() + "' AND chunkZ = '" + claim.getChunk().getZ() + "' ";
		try {
			execute(execution);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		ClaimKey key = new ClaimKey(claim.getWorldUUID(), claim.getChunk());
		adminclaims.remove(key);
	}
	
	public static void addTrust(UUID playerUUID, UUID worldUUID, Vector3i chunk) {		
		String execution = "INSERT INTO trustedplayers (playerUUID, worldUUID, chunkX, chunkZ) VALUES ('" + playerUUID.toString() + "', '" + worldUUID.toString() + "', '" + chunk.getX() + "', '" + chunk.getZ() + "')";
		try {
			execute(execution);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ClaimKey key = new ClaimKey(worldUUID, chunk);
		List<UUID> list = new ArrayList<>();
		if (trustedPlayers.containsKey(key)) {
			list = trustedPlayers.get(key);
			list.add(playerUUID);
			trustedPlayers.replace(key, list);
		} else {
			list.add(playerUUID);
			trustedPlayers.put(key, list);
		}
	}
	
	public static void removeTrust(UUID playerUUID, UUID worldUUID, Vector3i chunk) {
		String execution = "DELETE FROM trustedplayers WHERE playerUUID = '" + playerUUID.toString() + "' AND worldUUID = '" + worldUUID.toString() + "' AND chunkX = '" + chunk.getX() + "' AND chunkZ = '" + chunk.getZ() + "'";
		try {
			execute(execution);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ClaimKey key = new ClaimKey(worldUUID, chunk);
		List<UUID> list = trustedPlayers.get(key);
		list.remove(playerUUID);
		trustedPlayers.replace(key, list);
	}
	
	public static void removeAllTrusts(PlayerClaim claim) {
		String execution = "DELETE FROM trustedplayers WHERE worldUUID = '" + claim.getWorldUUID().toString() + "' AND chunkX = '" + claim.getChunk().getX() + "' AND chunkZ = '" + claim.getChunk().getZ() + "'";
		try {
			execute(execution);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ClaimKey key = new ClaimKey(claim.getWorldUUID(), claim.getChunk());
		if (trustedPlayers.containsKey(key)) trustedPlayers.remove(key);
	}
		
	public static void addFriend(UUID playerUUID, UUID friendUUID) {
		String execution = "INSERT INTO friends (playerUUID, friendUUID) VALUES ('" + playerUUID + "', '" + friendUUID + "')";
		try {
			execute(execution);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		List<UUID> list = new ArrayList<>();
		if (friendList.containsKey(playerUUID)) {
			list = friendList.get(playerUUID);
			list.add(friendUUID);
			friendList.replace(playerUUID, list);
		} else {
			list.add(friendUUID);
			friendList.put(playerUUID, list);
		}
		
	}
	
	public static void removeFriend(UUID playerUUID, UUID friendUUID) {
		String execution = "DELETE FROM friends WHERE playerUUID = '" + playerUUID + "' AND friendUUID = '" + friendUUID + "'";
		try {
			execute(execution);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		List<UUID> list = friendList.get(playerUUID);
		list.remove(friendUUID);
		friendList.replace(playerUUID, list);
	}
}
