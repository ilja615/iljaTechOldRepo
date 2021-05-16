package net.minecraft.loot;

import net.minecraft.util.ResourceLocation;

public class LootParameter<T> {
   private final ResourceLocation name;

   public LootParameter(ResourceLocation p_i51213_1_) {
      this.name = p_i51213_1_;
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public String toString() {
      return "<parameter " + this.name + ">";
   }
}
