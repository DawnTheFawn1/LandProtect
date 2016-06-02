package com.initianovamc.rysingdragon.landprotect.listeners;

import com.flowpowered.math.vector.Vector3i;
import com.initianovamc.rysingdragon.landprotect.database.LandProtectDB;
import com.initianovamc.rysingdragon.landprotect.utils.AdminClaim;
import com.initianovamc.rysingdragon.landprotect.utils.ClaimBoundary;
import com.initianovamc.rysingdragon.landprotect.utils.Messages;
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
import org.spongepowered.api.text.serializer.TextSerializers;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
					ItemStack i = optItem.get();
					if (i.getItem().equals(ItemTypes.LEATHER_BOOTS) && i.get(Keys.ITEM_ENCHANTMENTS).isPresent()) {
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
							ClaimBoundary boundary = new ClaimBoundary(player, newChunk);
							boundary.spawnTimedResetDelay(TimeUnit.SECONDS, 60);
							player.sendMessage(Text.of(TextColors.DARK_AQUA, "You have claimed this chunk"));
							
							if (durability <= 0) {
								player.setBoots(null);
								return;
							}
							return;
						}
					} else if (i.getItem().equals(ItemTypes.CHAINMAIL_BOOTS) && i.get(Keys.ITEM_ENCHANTMENTS).isPresent()) {
						List<ItemEnchantment> list = i.get(Keys.ITEM_ENCHANTMENTS).get();
						if (list.get(0).getLevel() == 10 && list.get(0).getEnchantment().equals(Enchantments.EFFICIENCY)) {
							LandProtectDB.addAdminClaim(new AdminClaim(worldUUID, newChunk));
							ClaimBoundary boundary = new ClaimBoundary(player, newChunk);
							boundary.spawnTimedResetDelay(TimeUnit.SECONDS, 60);
							player.sendMessage(Text.of(TextColors.DARK_AQUA, "You have claimed this chunk"));
						}
					}
				}
			}
			
			if (Utils.isClaimed(oldChunk, worldUUID) && !Utils.isClaimed(newChunk, worldUUID)) {
				player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Messages.UNCLAIMED_MESSAGE));
				
			} else if (!Utils.isPlayerClaimed(oldChunk, worldUUID) && Utils.isPlayerClaimed(newChunk, worldUUID)) {
				UUID ownerUUID = Utils.getClaimOwner(newChunk, worldUUID).get();
				UserStorageService service = Sponge.getGame().getServiceManager().provide(UserStorageService.class).get();
				User owner = service.get(ownerUUID).get();
				player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Messages.PLAYERCLAIM_MESSAGE.replace("@player", owner.getName())));
				
			} else if (Utils.isPlayerClaimed(oldChunk, worldUUID)&& Utils.isPlayerClaimed(newChunk, worldUUID)) {
				if (!Utils.getClaimOwner(oldChunk, worldUUID).get().equals(Utils.getClaimOwner(newChunk, worldUUID).get())) {
					UUID ownerUUID = Utils.getClaimOwner(newChunk, worldUUID).get();
					UserStorageService service = Sponge.getGame().getServiceManager().provide(UserStorageService.class).get();
					User owner = service.get(ownerUUID).get();
					player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Messages.PLAYERCLAIM_MESSAGE.replace("@player", owner.getName())));
				}
				
				
			} else if (!Utils.isAdminClaimed(oldChunk, worldUUID) && Utils.isAdminClaimed(newChunk, worldUUID)) {
				player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Messages.ADMINCLAIM_MESSAGE));
			}
		}
		
	}
	
}
