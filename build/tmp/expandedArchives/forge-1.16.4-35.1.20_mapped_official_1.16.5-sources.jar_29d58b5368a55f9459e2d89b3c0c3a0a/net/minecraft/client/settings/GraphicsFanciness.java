package net.minecraft.client.settings;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum GraphicsFanciness {
   FAST(0, "options.graphics.fast"),
   FANCY(1, "options.graphics.fancy"),
   FABULOUS(2, "options.graphics.fabulous");

   private static final GraphicsFanciness[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(GraphicsFanciness::getId)).toArray((p_238165_0_) -> {
      return new GraphicsFanciness[p_238165_0_];
   });
   private final int id;
   private final String key;

   private GraphicsFanciness(int p_i232238_3_, String p_i232238_4_) {
      this.id = p_i232238_3_;
      this.key = p_i232238_4_;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public GraphicsFanciness cycleNext() {
      return byId(this.getId() + 1);
   }

   public String toString() {
      switch(this) {
      case FAST:
         return "fast";
      case FANCY:
         return "fancy";
      case FABULOUS:
         return "fabulous";
      default:
         throw new IllegalArgumentException();
      }
   }

   public static GraphicsFanciness byId(int p_238163_0_) {
      return BY_ID[MathHelper.positiveModulo(p_238163_0_, BY_ID.length)];
   }
}
