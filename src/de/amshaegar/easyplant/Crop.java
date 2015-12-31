package de.amshaegar.easyplant;

import org.bukkit.Material;

public class Crop {
  private String name;
  private Material crop;
  private Material seed;

  public Crop(String name, Material seed, Material crop) {
    this.name = name;
    this.seed = seed;
    this.crop = crop;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Material getSeed() {
    return seed;
  }

  public void setSeed(Material seed) {
    this.seed = seed;
  }

  public Material getCrop() {
    return crop;
  }

  public void setCrop(Material crop) {
    this.crop = crop;
  }
}
