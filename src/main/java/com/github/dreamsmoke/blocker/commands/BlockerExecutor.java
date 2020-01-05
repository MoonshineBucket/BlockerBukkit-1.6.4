package com.github.dreamsmoke.blocker.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.dreamsmoke.blocker.BlockerBukkit;

public class BlockerExecutor implements CommandExecutor {
	
	private BlockerBukkit plugin;
	
	public BlockerExecutor(BlockerBukkit plugin) {
		this.plugin = plugin;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("blocker.own")) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9/blocker reload&7: &3Перезагрузить конфигурации.."));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9/blocker <add/remove>&7: &3Добавить/Удалить запрещенный предмет.."));
			} else {
				if(args[0].contains("reload")) {
					this.getPlugin().reloadConfig();
					this.getPlugin().saveConfig();
					this.getPlugin().load();
					
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Конфигурации перезагружены.."));
				}
				
				if((args[0].contains("add") || args[0].contains("remove")) && (sender instanceof Player)) {
					FileConfiguration config = this.getPlugin().getConfiguration();
					List<String> list = config.getStringList("banned");
					Player player = (Player) sender;
					switch (args[0]) {
					case "add":
						this.addItem(config, list, player);
						break;
					case "remove":
						this.removeItem(config, list, player);
						break;
					default:
						break;
					}
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cОшибка&7: &cДанная команда не может быть выполнена в терминале!"));
				}
			}
		}
		
		return false;
	}
	
	public BlockerBukkit getPlugin() {
		return this.plugin;
	}
	
	public void addItem(FileConfiguration config, List<String> list, Player player) {
		ItemStack itemStack = player.getItemInHand();
		if(itemStack == null) {
			return;
		}
		
		int itemId = itemStack.getTypeId();
		list.add(String.valueOf(itemId));
		config.set("banned", list);
		this.getPlugin().saveConfig();
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3Предмет успешно заблокирован!"));
	}
	
	public void removeItem(FileConfiguration config, List<String> list, Player player) {
		ItemStack itemStack = player.getItemInHand();
		if(itemStack == null) {
			return;
		}
		
		if(list.isEmpty()) {
			return;
		}
		
		int itemId = itemStack.getTypeId();
		list.remove(String.valueOf(itemId));
		config.set("banned", list);
		this.getPlugin().saveConfig();
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3Предмет успешно разблокирован!"));
	}

}
