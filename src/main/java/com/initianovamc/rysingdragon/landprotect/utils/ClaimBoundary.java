package com.initianovamc.rysingdragon.landprotect.utils;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.property.block.SkyLuminanceProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClaimBoundary {
	
	private Player player;
	private Vector3i chunk;
	private List<Location<World>> claimCorners;
	
	public ClaimBoundary(Player player, Vector3i chunk) {
		this.player = player;
		this.chunk = chunk;
		this.claimCorners = getAllCorners();
	}

	private Optional<Location<World>> getSurfaceBlock(Location<World> location) {
		boolean locationFound = false;
		int start = 0;
		int end = 256;
		
		while (!locationFound) {
			
			int middle = (start + end)/2;
			Location<World> newLocation = new Location<World>(location.getExtent(), location.getX(), middle, location.getZ());
			double lightLevel = newLocation.getProperty(SkyLuminanceProperty.class).get().getValue();
			
			if (start >= end) {
				return Optional.empty();
			}
			
			if (lightLevel>0 && newLocation.getExtent().getLocation(newLocation.getX(), newLocation.getY()-1, newLocation.getZ()).getProperty(SkyLuminanceProperty.class).get().getValue() <1) {
				locationFound = true;
				return Optional.of(newLocation);
			} 
			
			if (lightLevel > 0) {
				end = middle;
				newLocation = new Location<World>(newLocation.getExtent(), newLocation.getX(), middle, newLocation.getZ());
			} else {
				start = middle;
				newLocation = new Location<World>(newLocation.getExtent(), newLocation.getX(), middle, newLocation.getZ());
			}
			middle = (start + end)/2;
		}
		
		return Optional.empty();
	}

	public void spawn() {
		
		for (Location<World> location : this.claimCorners) {
			BlockType block = Sponge.getRegistry().getType(BlockType.class, Utils.getBoundaryBlock()).get();
			player.sendBlockChange(getSurfaceBlock(location).get().getBlockPosition(), BlockState.builder().blockType(block).build());
		}
		
	}
	
	public void reset() {
		
		for (Location<World> location : this.claimCorners) {
			player.resetBlockChange(getSurfaceBlock(location).get().getBlockPosition());
		}
		
	}
	
	private List<Location<World>> getFirstCorner() {
		
		List<Location<World>> corner = new ArrayList<>();
		
		Location<World> location = player.getWorld().getLocation((chunk.getX() * 16), 0, (chunk.getZ() * 16));
		Location<World> locationX = player.getWorld().getLocation(location.getX()+1, 0, location.getZ());
		Location<World> locationZ = player.getWorld().getLocation(location.getX(), 0, location.getZ()+1);
		
		corner.add(location);
		corner.add(locationX);
		corner.add(locationZ);
		return corner;
	}
	
	private List<Location<World>> getSecondCorner() {
		
		List<Location<World>> corner = new ArrayList<>();
		
		Location<World> location = player.getWorld().getLocation((chunk.getX() * 16)+15, 0, (chunk.getZ() * 16));
		Location<World> locationX = player.getWorld().getLocation(location.getX()-1, 0, location.getZ());
		Location<World> locationZ = player.getWorld().getLocation(location.getX(), 0, location.getZ()+1);
		
		corner.add(location);
		corner.add(locationX);
		corner.add(locationZ);
		return corner;
	}
	
	private List<Location<World>> getThirdCorner() {
		
		List<Location<World>> corner = new ArrayList<>();
		
		Location<World> location = player.getWorld().getLocation((chunk.getX() * 16), 0, (chunk.getZ() * 16)+15);
		Location<World> locationX = player.getWorld().getLocation(location.getX()+1, 0, location.getZ());
		Location<World> locationZ = player.getWorld().getLocation(location.getX(), 0, location.getZ()-1);
		
		corner.add(location);
		corner.add(locationX);
		corner.add(locationZ);
		return corner;
	}
	
	private List<Location<World>> getFourthCorner() {
		
		List<Location<World>> corner = new ArrayList<>();
		
		Location<World> location = player.getWorld().getLocation((chunk.getX() * 16)+15, 0, (chunk.getZ() * 16)+15);
		Location<World> locationX = player.getWorld().getLocation(location.getX()-1, 0, location.getZ());
		Location<World> locationZ = player.getWorld().getLocation(location.getX(), 0, location.getZ()-1);
		
		corner.add(location);
		corner.add(locationX);
		corner.add(locationZ);
		return corner;
	}
	
	private List<Location<World>> getAllCorners() {
		
		List<Location<World>> corners = new ArrayList<>();
		
		corners.addAll(getFirstCorner());
		corners.addAll(getSecondCorner());
		corners.addAll(getThirdCorner());
		corners.addAll(getFourthCorner());

		return corners;
	}
	
	
}
