package com.initianovamc.rysingdragon.landprotect.listeners;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
import com.initianovamc.rysingdragon.landprotect.utils.PlayerClaim;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.option.OptionSubject;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerMoveListener {

	@Listener
	public void onPlayerMove(DisplaceEntityEvent.TargetPlayer event) {
		Player player = event.getTargetEntity();
		Vector3i oldChunk = event.getFromTransform().getLocation().getChunkPosition();
		Vector3i newChunk = event.getToTransform().getLocation().getChunkPosition();
		UUID worldUUID = player.getWorld().getUniqueId();
		
		if (!oldChunk.equals(newChunk)) {
			
			if (!Utils.isClaimed(newChunk, worldUUID)) {
				Optional<ItemStack> optItem = player.getBoots();
				if (optItem.isPresent()) {
					if (optItem.get().getItem().equals(ItemTypes.LEATHER_BOOTS) && optItem.get().get(Keys.ITEM_ENCHANTMENTS).isPresent()) {
						ItemStack i = optItem.get();
						List<ItemEnchantment> list = i.get(Keys.ITEM_ENCHANTMENTS).get();
						if (list.get(0).getLevel() == 10 && list.get(0).getEnchantment().equals(Enchantments.EFFICIENCY)) {
							
							Subject subject = player;
							if (subject instanceof OptionSubject) {
								OptionSubject optSubject = (OptionSubject) subject;
								int claimLimit = Integer.parseInt(optSubject.getOption("claimlimit").orElse("0"));
								try {
									int claimAmount = LandProtectDB.getPlayerClaimAmount(player.getUniqueId());
									if (claimLimit != 0) {
										if (claimAmount >= claimLimit) {
											player.sendMessage(Text.of(TextColors.RED, "You have reached the max claim limit"));
											return;
										} 
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							
							
							int durabilityToRemove = (int) (65 * 0.05);
							int durability = i.get(Keys.ITEM_DURABILITY).get() - durabilityToRemove;
							i.offer(Keys.ITEM_DURABILITY, durability);
							player.setBoots(i);
							LandProtectDB.addPlayerClaim(new PlayerClaim(worldUUID, newChunk, player.getUniqueId()));
							player.sendMessage(Text.of("claimed this chunk"));
							if (durability <= 0) {
								player.setBoots(null);
								return;
							}
							return;
						}
					}
				}
			}
			
			if (Utils.isClaimed(oldChunk, worldUUID) && !Utils.isClaimed(newChunk, worldUUID)) {
				player.sendMessage(Text.of(TextColors.DARK_AQUA, "Now entering unclaimed land"));
				
			} else if (!Utils.isClaimed(oldChunk, worldUUID) && Utils.isClaimed(newChunk, worldUUID)) {
				
				if (Utils.getClaimOwner(newChunk, worldUUID).isPresent()) {
					UUID ownerUUID = Utils.getClaimOwner(newChunk, worldUUID).get();
					UserStorageService service = Sponge.getGame().getServiceManager().provide(UserStorageService.class).get();
					User owner = service.get(ownerUUID).get();
					player.sendMessage(Text.of(TextColors.DARK_AQUA, "Now entering the land of ", TextColors.GOLD, owner.getName()));
				} else {
					player.sendMessage(Text.of(TextColors.DARK_AQUA, "Now entering ", TextColors.GOLD, "AdminClaim"));
				}
				
			} else if (Utils.isClaimed(oldChunk, worldUUID) && Utils.isClaimed(newChunk, worldUUID)) {
				
				if (Utils.isAdminClaimed(oldChunk, worldUUID) && Utils.isAdminClaimed(newChunk, worldUUID)) {
					return;
					
				} else if (Utils.getClaimOwner(oldChunk, worldUUID).isPresent() && Utils.isAdminClaimed(newChunk, worldUUID)) {
					player.sendMessage(Text.of(TextColors.DARK_AQUA, "Now entering ", TextColors.GOLD, "AdminClaim"));
					
				} else if (Utils.getClaimOwner(newChunk, worldUUID).isPresent()) {
					
					if (Utils.getClaimOwner(oldChunk, worldUUID).isPresent()) {
						if (Utils.getClaimOwner(oldChunk, worldUUID).get().equals(Utils.getClaimOwner(newChunk, worldUUID).get())) {
							return;
						}
					}
					
					UUID ownerUUID = Utils.getClaimOwner(newChunk, worldUUID).get();
					UserStorageService service = Sponge.getGame().getServiceManager().provide(UserStorageService.class).get();
					User owner = service.get(ownerUUID).get();
					player.sendMessage(Text.of(TextColors.DARK_AQUA, "Now entering the land of ", TextColors.GOLD, owner.getName()));
				}
				
			}
		}
		
	}
	
}
