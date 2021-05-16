package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum PistonType implements IStringSerializable {
   DEFAULT("normal"),
   STICKY("sticky");

   private final String name;

   private PistonType(String p_i49335_3_) {
      this.name = p_i49335_3_;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }
}
