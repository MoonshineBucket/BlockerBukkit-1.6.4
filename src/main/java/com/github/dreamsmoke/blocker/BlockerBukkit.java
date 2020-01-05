package com.github.dreamsmoke.blocker;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.dreamsmoke.blocker.commands.BlockerExecutor;
import com.github.dreamsmoke.blocker.listeners.BlockerListener;

public class BlockerBukkit extends JavaPlugin {
	
	static BlockerBukkit instance;
	private FileConfiguration config;
	private Logger logger;
	
	
	@Override
	public void onEnable() {
		instance = this;
		logger = Logger.getLogger("Minecraft");
		
		this.load();
		Server server = this.getServer();
		PluginManager manager = server.getPluginManager();
		this.getCommand("blocker").setExecutor(new BlockerExecutor(this));
		manager.registerEvents(new BlockerListener(this), this);
	}
	
	public static BlockerBukkit getInstance() {
		return instance == null?(new BlockerBukkit()):instance;
	}
	
	public FileConfiguration getConfiguration() {
		return this.config;
	}
	
	public void info(String msg) {
		this.logger.info("[QuestionBukkit] " + msg);
	}
	
	public void load() {
		this.info("Loading config..");
		File configFile = new File(this.getDataFolder(), "config.yml");
		if(!configFile.exists()) {
			this.saveDefaultConfig();
			info("Created default config..");
		}
		
		this.config = this.getConfig();
		this.info("Configuration loaded..");
	}

}
