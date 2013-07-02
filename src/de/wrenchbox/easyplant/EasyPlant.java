package de.wrenchbox.easyplant;

import java.io.IOException;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyPlant extends JavaPlugin {

	private static Plugin plugin;

	@Override
	public void onEnable() {
		plugin = this;
		
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			getLogger().info("Unable to connect to metrics server.");
		}
		
		new PlantListener();
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll();
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
	
}
