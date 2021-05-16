package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum AttachFace implements IStringSerializable {
   FLOOR("floor"),
   WALL("wall"),
   CEILING("ceiling");

   private final String name;

   private AttachFace(String p_i49343_3_) {
      this.name = p_i49343_3_;
   }

   public String getSerializedName() {
      return this.name;
   }
}
