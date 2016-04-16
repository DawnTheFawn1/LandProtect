package com.initianovamc.rysingdragon.landprotect.utils;

import com.flowpowered.math.vector.Vector3i;

import java.util.UUID;

public class PlayerClaim {

	private Vector3i chunk;		
	private UUID worldUUID;
	private UUID playerUUID;
	
	public PlayerClaim(UUID worldUUID, Vector3i chunk, UUID playerUUID) {
		this.chunk = chunk;
		this.worldUUID = worldUUID;
		this.playerUUID = playerUUID;
	}
	
	public Vector3i getChunk() {
		return this.chunk;
	}
	
	public UUID getPlayerUUID() {
		return this.playerUUID;
	}
	
	public UUID getWorldUUID() {
		return this.worldUUID;
	}
	
}
