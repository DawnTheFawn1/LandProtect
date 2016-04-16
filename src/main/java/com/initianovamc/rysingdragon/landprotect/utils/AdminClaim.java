package com.initianovamc.rysingdragon.landprotect.utils;

import com.flowpowered.math.vector.Vector3i;

import java.util.UUID;

public class AdminClaim {

	private UUID worldUUID;
	private Vector3i chunk;
	
	public AdminClaim(UUID worldUUID, Vector3i chunk) {
		this.worldUUID = worldUUID;
		this.chunk = chunk;
	}
	
	public Vector3i getChunk() {
		return this.chunk;
	}
	
	public UUID getWorldUUID() {
		return this.worldUUID;
	}
	
}
