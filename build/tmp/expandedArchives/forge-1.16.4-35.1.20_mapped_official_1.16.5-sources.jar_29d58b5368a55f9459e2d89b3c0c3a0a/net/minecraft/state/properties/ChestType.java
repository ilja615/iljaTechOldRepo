package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum ChestType implements IStringSerializable {
   SINGLE("single", 0),
   LEFT("left", 2),
   RIGHT("right", 1);

   public static final ChestType[] BY_ID = values();
   private final String name;
   private final int opposite;

   private ChestType(String p_i49341_3_, int p_i49341_4_) {
      this.name = p_i49341_3_;
      this.opposite = p_i49341_4_;
   }

   public String getSerializedName() {
      return this.name;
   }

   public ChestType getOpposite() {
      return BY_ID[this.opposite];
   }
}
