package com.initianovamc.rysingdragon.landprotect.utils;

import com.flowpowered.math.vector.Vector3i;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class ClaimKey {

	private Vector3i chunkKey;
	private UUID worldKey;
	
	public ClaimKey(UUID worldUUID, Vector3i chunk) {
		this.chunkKey = chunk;
		this.worldKey = worldUUID;
	}
	
	public Vector3i getChunkKey() {
		return this.chunkKey;
	}
	
	public UUID getWorldKey() {
		return this.worldKey;
	}
	
	public void setChunkKey(Vector3i chunk) {
		this.chunkKey = chunk;
	}
	
	public void setWorldKey(UUID worldUUID) {
		this.worldKey = worldUUID;
	}
	
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ClaimKey) {
			ClaimKey key = (ClaimKey)obj;
			if (key.worldKey.equals(this.worldKey) && key.chunkKey.equals(this.chunkKey)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(37, 51).append(chunkKey).append(worldKey).toHashCode();
	}
	
}
