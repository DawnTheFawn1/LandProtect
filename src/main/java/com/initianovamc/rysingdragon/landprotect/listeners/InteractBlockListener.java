package com.initianovamc.rysingdragon.landprotect.listeners;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.initianovamc.rysingdragon.landprotect.config.GeneralConfig;
import com.initianovamc.rysingdragon.landprotect.utils.ClaimBoundary;
import com.initianovamc.rysingdragon.landprotect.utils.ClaimKey;
import com.initianovamc.rysingdragon.landprotect.utils.Utils;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InteractBlockListener {

	@Listener
	public void onInteract(InteractBlockEvent.Secondary event, @First Player player) {
		if (event.getCause().containsType(Player.class)) {
			
			if (player.getItemInHand().isPresent()) {
				ItemStack itemStack = player.getItemInHand().get();
				if (event.getTargetBlock().getState().getType().equals(BlockTypes.ENCHANTING_TABLE)) {
					int level = player.get(Keys.EXPERIENCE_LEVEL).get();
					if (level >= 10) {
						if (itemStack.getItem().equals(ItemTypes.LEATHER_BOOTS)) {
							event.setCancelled(true);
							ItemEnchantment ench = new ItemEnchantment(Enchantments.EFFICIENCY, 10);
							List<ItemEnchantment> list = Arrays.asList(ench);
							itemStack.offer(Keys.ITEM_ENCHANTMENTS, list);
							itemStack.offer(Keys.HIDE_ENCHANTMENTS, true);
							itemStack.offer(Keys.DISPLAY_NAME, Text.of("Claiming boots"));
							itemStack.offer(Keys.ITEM_LORE, Arrays.asList(Text.of("boots that will claim land as you walk")));
							itemStack.offer(Keys.ITEM_DURABILITY, 65);
							player.setItemInHand(itemStack);
							player.offer(Keys.EXPERIENCE_LEVEL, level - 10);
						}
					}
				}
				
				ItemType item = Sponge.getRegistry().getType(ItemType.class, Utils.getClaimInspectTool()).get();
				if (itemStack.getItem().getType().equals(item)) {
					ClaimKey key = new ClaimKey(player.getWorld().getUniqueId(), player.getLocation().getChunkPosition());
					if (Utils.claimBoundaries.containsKey(key)) {
						ClaimBoundary boundary = Utils.claimBoundaries.get(key);
						boundary.reset();
						Utils.claimBoundaries.remove(key);
						} else {
							if (Utils.isClaimed(player.getLocation().getChunkPosition(), player.getWorld().getUniqueId())) {
								ClaimBoundary boundary = new ClaimBoundary(player, player.getLocation().getChunkPosition());
								boundary.spawn();
								Utils.claimBoundaries.put(key, boundary);
							}
						}
					} 
				}
			}
			
			UUID worldUUID = player.getWorld().getUniqueId();

			if (Utils.inAddInteractMode.contains(player.getUniqueId())) {
				Utils.inAddInteractMode.remove(player.getUniqueId());
				event.setCancelled(true);
				try {
					List<String> interactables = GeneralConfig.getConfig().getConfigNode().getNode("Interactable").getList(TypeToken.of(String.class), new ArrayList<>());
					if (interactables.contains(event.getTargetBlock().getState().getType().getId())) {
						player.sendMessage(Text.of(TextColors.RED, "This block is already added as interactable"));
						return;
					}
					interactables.add(event.getTargetBlock().getState().getType().getId());
					GeneralConfig.getConfig().getConfigNode().getNode("Interactable").setValue(interactables);
					GeneralConfig.getConfig().save();
					player.sendMessage(Text.of(TextColors.DARK_AQUA, "id ", TextColors.GOLD, event.getTargetBlock().getState().getType().getName(), TextColors.DARK_AQUA, " has been added to the list of interactable blocks"));
				} catch (ObjectMappingException e) {
					e.printStackTrace();
				}
			}
			
			if (Utils.inRemoveInteractMode.contains(player.getUniqueId())) {
				Utils.inRemoveInteractMode.remove(player.getUniqueId());
				event.setCancelled(true);
				try {
					List<String> interactables = GeneralConfig.getConfig().getConfigNode().getNode("Interactable").getList(TypeToken.of(String.class), new ArrayList<>());
					if (!interactables.contains(event.getTargetBlock().getState().getType().getId())) {
						player.sendMessage(Text.of(TextColors.RED, "This block has not yet been added as interactable"));
						return;
					}
					interactables.remove(event.getTargetBlock().getState().getType().getId());
					GeneralConfig.getConfig().getConfigNode().getNode("Interactable").setValue(interactables);
					GeneralConfig.getConfig().save();
					player.sendMessage(Text.of(TextColors.DARK_AQUA, "id ", TextColors.GOLD, event.getTargetBlock().getState().getType().getName(), TextColors.DARK_AQUA, " has been removed from the list of interactable blocks"));
				} catch (ObjectMappingException e) {
					e.printStackTrace();
				}
			}
			
			if (event.getTargetBlock().getLocation().isPresent()) {
				Vector3i chunk = event.getTargetBlock().getLocation().get().getChunkPosition();
				
				if (Utils.isClaimed(chunk, worldUUID)) {
				
					if (Utils.isAdminClaimed(chunk, worldUUID)) {
						
						if (player.hasPermission("landprotect.protect.bypass")) {
							return;
						}
						
						try {
							List<String> interactables = GeneralConfig.getConfig().getConfigNode().getNode("Interactable").getList(TypeToken.of(String.class), new ArrayList<>());
							if (!interactables.contains(event.getTargetBlock().getState().getType().getName())) {
								event.setCancelled(true);
								player.sendMessage(Text.of(TextColors.RED, "This land is claimed"));
								return;
							} else {
								return;
							}
						} catch (ObjectMappingException e) {
							e.printStackTrace();
						}
						
					}
					
					if (Utils.isTrustedToClaim(chunk, player.getUniqueId(), worldUUID)) {
						return;
					}
					
					if (Utils.getClaimOwner(chunk, worldUUID).isPresent()) {
						UUID owner = Utils.getClaimOwner(chunk, worldUUID).get();
						if (owner.equals(player.getUniqueId())) {
							return;
						} else if (Utils.isFriend(owner, player.getUniqueId())) {
							return;
						}
					}
					
					if (player.hasPermission("landprotect.claim.bypass")) {
						return;
					}	
					event.setCancelled(true);
					player.sendMessage(Text.of(TextColors.RED, "This land is claimed"));
				}
			}
		}
	}
