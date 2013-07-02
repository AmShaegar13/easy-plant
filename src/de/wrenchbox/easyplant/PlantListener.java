package de.wrenchbox.easyplant;

import java.util.LinkedList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlantListener implements Listener {

	private LinkedList<Block> queue = new LinkedList<Block>(),
			visited = new LinkedList<Block>();

	public PlantListener() {
		EasyPlant.getPlugin().getServer().getPluginManager().registerEvents(this, EasyPlant.getPlugin());
	}

	@EventHandler
	public void onPlant(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Player player = event.getPlayer();

		if(event.getClickedBlock().getType() == Material.SOIL && event.getItem() != null
				&& (event.getItem().getType() == Material.SEEDS
						|| event.getItem().getType() == Material.POTATO_ITEM
						|| event.getItem().getType() == Material.CARROT_ITEM
						|| event.getItem().getType() == Material.MELON_SEEDS
						|| event.getItem().getType() == Material.PUMPKIN_SEEDS)) {
			if(!player.hasPermission("easyplant.plant")) {
				return;
			}

			Block previous;
			queue.add(event.getClickedBlock());
			Material type = event.getItem().getType() == Material.POTATO_ITEM ? Material.POTATO :
				event.getItem().getType() == Material.CARROT_ITEM ? Material.CARROT :
					event.getItem().getType() == Material.MELON_SEEDS ? Material.MELON_STEM :
						event.getItem().getType() == Material.PUMPKIN_SEEDS ? Material.PUMPKIN_STEM : Material.CROPS;
			while((previous = queue.poll()) != null) {
				for(int i = 0; i < 4; i++) {
					if(event.getItem().getAmount() == 1) {
						break;
					}
					Block relative = previous.getRelative(BlockFace.values()[i]).getRelative(BlockFace.DOWN);
					for(int j = 0; j < 3; j++) {
						if(relative.getType() == Material.SOIL) {
							if(!event.getClickedBlock().equals(relative) && !visited.contains(relative)) {
								queue.add(relative);
								visited.add(relative);
								if(relative.getRelative(BlockFace.UP).isEmpty()) {
									event.getItem().setAmount(event.getItem().getAmount()-1);
									relative.getRelative(BlockFace.UP).setType(type);
								}
							}
						} else {
							relative = relative.getRelative(BlockFace.UP);
						}
					}
				}
			}
			queue.clear();
			visited.clear();
		}
	}
}
