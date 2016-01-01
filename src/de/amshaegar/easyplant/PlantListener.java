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

  private final Map<Material, Crop[]> types = new HashMap<>();
  private final LinkedList<Block> visited = new LinkedList<>();
  private final LinkedList<Block> queue = new LinkedList<>();

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

    plantAdjacents(block, item, crop.getCrop());
  }

  private void plantAdjacents(final Block clickedBlock, final ItemStack placedItem, final Material cropType) {
    System.out.println(clickedBlock + " start!");
    final Material soilType = clickedBlock.getType();
    Block previousBlock;
    queue.add(clickedBlock);
    visited.add(clickedBlock);
    while((previousBlock = queue.poll()) != null && placedItem.getAmount() > 1) {
      Block base = previousBlock.getRelative(BlockFace.DOWN);
      for(BlockFace dir : CARDINAL_DIRS) {
        Block relative = base.getRelative(dir);
        while(relative.getLocation().getBlockY() - 1 <= previousBlock.getLocation().getBlockY()) {
          if(relative.getType() == soilType && !visited.contains(relative)) {
            queue.add(relative);
            visited.add(relative);
            plantCrop(placedItem, cropType, relative);
          } else {
            relative = relative.getRelative(BlockFace.UP);
          }
        }
      }
    }
    queue.clear();
    visited.clear();
  }

  private void plantCrop(final ItemStack placedItem, final Material cropType, final Block relative) {
    final Block top = relative.getRelative(BlockFace.UP);
    if(top.isEmpty()) {
      placedItem.setAmount(placedItem.getAmount() - 1);
      top.setType(cropType);
    }
  }
}
