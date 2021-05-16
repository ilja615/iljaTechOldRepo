package net.minecraft.entity.passive.horse;

import java.util.Arrays;
import java.util.Comparator;

public enum CoatColors {
   WHITE(0),
   CREAMY(1),
   CHESTNUT(2),
   BROWN(3),
   BLACK(4),
   GRAY(5),
   DARKBROWN(6);

   private static final CoatColors[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(CoatColors::getId)).toArray((p_234255_0_) -> {
      return new CoatColors[p_234255_0_];
   });
   private final int id;

   private CoatColors(int p_i231559_3_) {
      this.id = p_i231559_3_;
   }

   public int getId() {
      return this.id;
   }

   public static CoatColors byId(int p_234254_0_) {
      return BY_ID[p_234254_0_ % BY_ID.length];
   }
}
