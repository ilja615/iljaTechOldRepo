package net.minecraft.block;

import net.minecraft.util.IStringSerializable;

public enum WallHeight implements IStringSerializable {
   NONE("none"),
   LOW("low"),
   TALL("tall");

   private final String name;

   private WallHeight(String p_i231882_3_) {
      this.name = p_i231882_3_;
   }

   public String toString() {
      return this.getSerializedName();
   }

   public String getSerializedName() {
      return this.name;
   }
}
