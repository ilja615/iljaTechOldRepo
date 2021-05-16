package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum BellAttachment implements IStringSerializable {
   FLOOR("floor"),
   CEILING("ceiling"),
   SINGLE_WALL("single_wall"),
   DOUBLE_WALL("double_wall");

   private final String name;

   private BellAttachment(String p_i49956_3_) {
      this.name = p_i49956_3_;
   }

   public String getSerializedName() {
      return this.name;
   }
}
