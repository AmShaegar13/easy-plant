package de.wrenchbox.easyplant;

import java.util.HashMap;
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
		if(player.isSneaking()) {
			return;
		}

		HashMap<Material, String> types = new HashMap<Material, String>();
		types.put(Material.SEEDS, "seeds");
		types.put(Material.POTATO_ITEM, "potato");
		types.put(Material.CARROT_ITEM, "carrot");
		types.put(Material.MELON_SEEDS, "melon");
		types.put(Material.PUMPKIN_SEEDS, "pumpkin");
		
		if(event.getClickedBlock().getType() == Material.SOIL && event.getItem() != null && types.keySet().contains(event.getItem().getType())) {
			if(!player.hasPermission("easyplant."+types.get(event.getItem().getType()))) {
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
