package com.initianovamc.rysingdragon.landprotect.utils;

import java.util.UUID;

public class FriendRequest {

	private UUID requester;
	private UUID friend;
	
	public FriendRequest(UUID requester, UUID friend) {
		this.requester = requester;
		this.friend = friend;
	}
	
	public UUID getRequester() {
		return this.requester;
	}
	
	public UUID getFriend() {
		return this.friend;
	}
	
}
