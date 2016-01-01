package de.amshaegar.easyplant;

import org.bukkit.Material;

public class Crop {
  private String name;
  private Material crop;
  private Material seed;

  public Crop(final String name, final Material seed, final Material crop) {
    this.name = name;
    this.seed = seed;
    this.crop = crop;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Material getSeed() {
    return seed;
  }

  public void setSeed(final Material seed) {
    this.seed = seed;
  }

  public Material getCrop() {
    return crop;
  }

  public void setCrop(final Material crop) {
    this.crop = crop;
  }
}
