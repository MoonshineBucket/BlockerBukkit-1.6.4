package com.github.dreamsmoke.blocker.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.dreamsmoke.blocker.BlockerBukkit;

public class BlockerListener implements Listener {
	
	private BlockerBukkit plugin;
	
	public BlockerListener(BlockerBukkit plugin) {
		this.plugin = plugin;
	}
	
	
	private BlockerBukkit getPlugin() {
		return this.plugin;
	}
	
	@EventHandler
	public void onPlayerPickup(PlayerPickupItemEvent e) {
		Player player = e.getPlayer();
		ItemStack itemStack = e.getItem().getItemStack();
		Location location = player.getLocation();
		World world = location.getWorld();
		if(itemStack == null) {
			return;
		}
		
		if(player.isOp()) {
			return;
		}
		
		boolean isBanned = this.isBanned(itemStack);
		if(isBanned) {
			e.setCancelled(true);
			this.handleSlots(player, false);
			HashMap<Item, Entity> saved = (new HashMap<Item, Entity>());
			List<Entity> entities = player.getNearbyEntities(5, 5, 5);
			for(Entity entity : entities) {
				if(entity.getType() == EntityType.DROPPED_ITEM) {
					saved.put((Item) entity, entity);
				}
			}
			
			if(saved.isEmpty()) {
				return;
			}
			
			for(Item item : saved.keySet()) {
				ItemStack removeStack = item.getItemStack();
				boolean isRemove = (removeStack == itemStack);
				if(isRemove) {
					Entity entity = saved.get(item);
					entity.remove();
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack itemStack = e.getItem();
		if(itemStack == null) {
			return;
		}
		
		if(player.isOp()) {
			return;
		}
		
		boolean isBanned = this.isBanned(itemStack);
		if(isBanned) {
			e.setCancelled(true);
			this.handleSlots(player, true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		ItemStack itemStack = e.getCurrentItem();
		if(itemStack == null) {
			return;
		}
		
		if(player.isOp()) {
			return;
		}
		
		boolean isBanned = this.isBanned(itemStack);
		if(isBanned) {
			e.setCancelled(true);
			this.handleSlots(player, false);
			PlayerInventory inventory = player.getInventory();
			inventory.remove(itemStack);
			player.updateInventory();
		}
	}
	
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		ItemStack itemStack = e.getItemDrop().getItemStack();
		if(itemStack == null) {
			return;
		}
		
		if(player.isOp()) {
			return;
		}
		
		boolean isBanned = this.isBanned(itemStack);
		if(isBanned) {
			e.setCancelled(true);
			this.handleSlots(player, false);
			PlayerInventory inventory = player.getInventory();
			inventory.remove(itemStack);
			player.updateInventory();
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		Block block = e.getBlock();
		if(block == null) {
			return;
		}
		
		ItemStack itemStack = (new ItemStack(Material.getMaterial(block.getTypeId())));
		if(player.isOp()) {
			return;
		}
		
		boolean isBanned = this.isBanned(itemStack);
		if(isBanned) {
			e.setCancelled(true);
			this.handleSlots(player, false);
		}
	}
	
	private boolean isBanned(ItemStack itemStack) {
		boolean cancel = false;
		ItemStack validate;
		FileConfiguration config = this.getPlugin().getConfiguration();
		for(String bans : config.getStringList("banned")) {
			String[] data = bans.split(":");
			int getDurability = -1, getId = -1;
			
			if(data.length == 1) {
				getId = Integer.parseInt(data[0]);
			} else if(data.length == 2) {
				getId = Integer.parseInt(data[0]);
				getDurability = Integer.parseInt(data[1]);
			}
			
			if(getId != -1) {
				validate = (new ItemStack(Material.getMaterial(getId)));
				if(getDurability != -1) {
					validate.setDurability((short) getDurability);
				}
				
				if(itemStack.getTypeId() == validate.getTypeId()) {
					if(validate.getDurability() == 0) {
						cancel = true;
					} else {
						if(itemStack.getDurability() == validate.getDurability()) {
							cancel = true;
						}
					}
				}
			}
		}
		
		return cancel;
	}
	
	public void handleSlots(Player player, boolean validate) {
		PlayerInventory inventory = player.getInventory();
		int slot = inventory.getHeldItemSlot();
		player.closeInventory();
		if(validate) {
			if(slot >= 1 && slot <= 5) {
				inventory.setHeldItemSlot(slot - 1);
			} else {
				inventory.setHeldItemSlot(slot + 1);
			}
			
			inventory.setItem(slot, (new ItemStack(Material.AIR)));
		}
		
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cОшибка&7: &cДанный предмет запрещен в использовании.."));
		player.updateInventory();
	}

}
