package net.minecraft.data;

import com.google.gson.JsonPrimitive;
import net.minecraft.util.ResourceLocation;

public class BlockModelFields {
   public static final BlockModeInfo<BlockModelFields.Rotation> X_ROT = new BlockModeInfo<>("x", (p_240207_0_) -> {
      return new JsonPrimitive(p_240207_0_.value);
   });
   public static final BlockModeInfo<BlockModelFields.Rotation> Y_ROT = new BlockModeInfo<>("y", (p_240205_0_) -> {
      return new JsonPrimitive(p_240205_0_.value);
   });
   public static final BlockModeInfo<ResourceLocation> MODEL = new BlockModeInfo<>("model", (p_240206_0_) -> {
      return new JsonPrimitive(p_240206_0_.toString());
   });
   public static final BlockModeInfo<Boolean> UV_LOCK = new BlockModeInfo<>("uvlock", JsonPrimitive::new);
   public static final BlockModeInfo<Integer> WEIGHT = new BlockModeInfo<>("weight", JsonPrimitive::new);

   public static enum Rotation {
      R0(0),
      R90(90),
      R180(180),
      R270(270);

      private final int value;

      private Rotation(int p_i232542_3_) {
         this.value = p_i232542_3_;
      }
   }
}
