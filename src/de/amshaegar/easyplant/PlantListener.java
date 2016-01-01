package de.amshaegar.easyplant;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PlantListener implements Listener {

  // NORTH, EAST, SOUTH, WEST
  private static final BlockFace[] CARDINAL_DIRS = Arrays.copyOfRange(BlockFace.values(), 0, 4);

  private Map<Material, Crop[]> types = new HashMap<>();

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
  public void onPlant(final PlayerInteractEvent event) {
    if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    final Block block = event.getClickedBlock();
    final ItemStack item = event.getItem();

    if(block == null || item == null) {
      return;
    }

    final Player player = event.getPlayer();
    if(player.isSneaking()) {
      return;
    }

    final Material soilType = block.getType();
    if(!types.keySet().contains(soilType)) {
      return;
    }
    final Material seedType = item.getType();
    Crop crop = null;

    for(Crop c : types.get(soilType)) {
      if(c.getSeed() == seedType) {
        crop = c;
        break;
      }
    }

    if(crop == null) {
      return;
    }

    if(!player.hasPermission("easyplant." + crop.getName())) {
      return;
    }

    plantAdjacent(block, item, crop.getCrop());
  }

  private void plantAdjacent(final Block clickedBlock, final ItemStack placedItem, final Material cropType) {
    final Material soilType = clickedBlock.getType();
    final LinkedList<Block> visited = new LinkedList<>();
    final LinkedList<Block> queue = new LinkedList<>();
    Block previousBlock;
    queue.add(clickedBlock);
    while((previousBlock = queue.poll()) != null) {
      for(BlockFace dir : CARDINAL_DIRS) {
        if(placedItem.getAmount() == 1) {
          return;
        }
        Block relative = previousBlock.getRelative(dir).getRelative(BlockFace.DOWN);
        for(int i = 0; i < 3; i++) {
          if(relative.getType() == soilType) {
            if(!clickedBlock.equals(relative) && !visited.contains(relative)) {
              queue.add(relative);
              visited.add(relative);
              final Block top = relative.getRelative(BlockFace.UP);
              if(top.isEmpty()) {
                placedItem.setAmount(placedItem.getAmount() - 1);
                top.setType(cropType);
              }
            }
          } else {
            relative = relative.getRelative(BlockFace.UP);
          }
        }
      }
    }
  }
}
