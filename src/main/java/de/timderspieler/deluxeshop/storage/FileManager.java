package de.timderspieler.deluxeshop.storage;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.timderspieler.deluxeshop.main.DeluxeShop;

public class FileManager {
	
	private DeluxeShop getPlugin() { return DeluxeShop.getPlugin(); }
	
	private File config_file = new File(getPlugin().getDataFolder(), "config.yml");
	private File data_file = new File(getPlugin().getDataFolder(), "data.yml");
	
	private FileConfiguration config;
	private FileConfiguration data;
	
	public FileManager() {
		
		loadFiles();
		
	}
	
	private void loadFiles() {
		
		if (!config_file.exists()) {
			getPlugin().saveResource("config.yml", false);
		}
		
		if (!data_file.exists()) {
			getPlugin().saveResource("data.yml", false);
		}
		
		this.config = YamlConfiguration.loadConfiguration(config_file);
		this.data = YamlConfiguration.loadConfiguration(data_file);
		
	}
	
	public void saveDataFile() {
		
		try {
			data.save(data_file);
		} catch (IOException e) {
			DeluxeShop.sendConsoleError("Error while saving data.yml");
		}
		
	}
	
	public FileConfiguration getDefaultConfig() { return config; }
	public FileConfiguration getDataConfig() { return data; }
	
}
