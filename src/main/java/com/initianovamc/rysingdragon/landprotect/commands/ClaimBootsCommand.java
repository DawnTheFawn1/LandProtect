package com.initianovamc.rysingdragon.landprotect.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Type;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.Optional;

public class ClaimBootsCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (src instanceof Player) {
			
			Player player = (Player) src;
			ItemStack stack = ItemStack.builder().quantity(1).itemType(ItemTypes.CHAINMAIL_BOOTS).keyValue(Keys.ITEM_ENCHANTMENTS, Arrays.asList(new ItemEnchantment(Enchantments.EFFICIENCY, 10))).build();
			stack.offer(Keys.HIDE_ENCHANTMENTS, true);
			stack.offer(Keys.DISPLAY_NAME, Text.of("AdminClaim Boots"));
			stack.offer(Keys.ITEM_LORE, Arrays.asList(Text.of("Boots that will claim land as"), Text.of("admin claim as you walk")));
			InventoryTransactionResult result = player.getInventory().query(GridInventory.class).offer(stack);
			if (result.getType().equals(Type.FAILURE) && !result.getRejectedItems().isEmpty()) {
				Location<World> location = player.getLocation();
				Optional<Entity> optEntity = location.getExtent().createEntity(EntityTypes.ITEM, location.getPosition());
				if (optEntity.isPresent()) {
					Item item = (Item) optEntity.get();
					item.offer(item.getItemData().item().set(stack.createSnapshot()));
					location.getExtent().spawnEntity(item, Cause.source(EntitySpawnCause.builder().entity(item).type(SpawnTypes.PLUGIN).build()).build());
				}
			}
			return CommandResult.success();
		} else {
			src.sendMessage(Text.of("You must be a player to use this command"));
			return CommandResult.empty();
		}
		
	}

}
