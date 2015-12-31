package de.wrenchbox.easyplant;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlantListener implements Listener {

  private Map<Material, Crop[]> types = new HashMap<>();
  private LinkedList<Block> queue = new LinkedList<>(),
      visited = new LinkedList<>();

  public PlantListener() {
    EasyPlant.getPlugin().getServer().getPluginManager().registerEvents(this, EasyPlant.getPlugin());

    Crop[] soilCrops = {
        new Crop("seeds", Material.SEEDS, Material.CROPS),
        new Crop("potato", Material.POTATO_ITEM, Material.POTATO),
        new Crop("carrot", Material.CARROT_ITEM, Material.CARROT),
        new Crop("melon", Material.MELON_SEEDS, Material.MELON_STEM),
        new Crop("pumpkin", Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM)
    };
    types.put(Material.SOIL, soilCrops);

    Crop[] soulSandCrops = {
        new Crop("nether_wart", Material.NETHER_STALK, Material.NETHER_WARTS)
    };
    types.put(Material.SOUL_SAND, soulSandCrops);
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

    Material soilType = event.getClickedBlock().getType();
    if(!types.keySet().contains(soilType)) {
      return;
    }

    Crop crop = null;
    Material seedType = event.getItem().getType();

    for(Crop c : types.get(soilType)) {
      if(c.getSeed() == seedType) {
        crop = c;
        break;
      }
    }

    if(crop == null) {
      return;
    }

    if(!player.hasPermission("easyplant."+crop.getName())) {
      return;
    }

    plantAdjacent(event.getClickedBlock(), event.getItem(), crop.getCrop());
  }

  private void plantAdjacent(Block clickedBlock, ItemStack placedItem, Material cropType) {
    Material soilType = clickedBlock.getType();
    Block previousBlock;
    queue.add(clickedBlock);
    while((previousBlock = queue.poll()) != null) {
      for(int i = 0; i < 4; i++) {
        if(placedItem.getAmount() == 1) {
          break;
        }
        Block relative = previousBlock.getRelative(BlockFace.values()[i]).getRelative(BlockFace.DOWN);
        for(int j = 0; j < 3; j++) {
          if(relative.getType() == soilType) {
            if(!clickedBlock.equals(relative) && !visited.contains(relative)) {
              queue.add(relative);
              visited.add(relative);
              Block top = relative.getRelative(BlockFace.UP);
              if(top.isEmpty()) {
                placedItem.setAmount(placedItem.getAmount()-1);
                top.setType(cropType);
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
