package de.amshaegar.easyplant;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyPlant extends JavaPlugin {

  private static Plugin plugin;

  @Override
  public void onEnable() {
    plugin = this;
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
