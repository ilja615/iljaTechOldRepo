package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;

public class WoodType {
   private static final Set<WoodType> VALUES = new ObjectArraySet<>();
   public static final WoodType OAK = register(new WoodType("oak"));
   public static final WoodType SPRUCE = register(new WoodType("spruce"));
   public static final WoodType BIRCH = register(new WoodType("birch"));
   public static final WoodType ACACIA = register(new WoodType("acacia"));
   public static final WoodType JUNGLE = register(new WoodType("jungle"));
   public static final WoodType DARK_OAK = register(new WoodType("dark_oak"));
   public static final WoodType CRIMSON = register(new WoodType("crimson"));
   public static final WoodType WARPED = register(new WoodType("warped"));
   private final String name;

   protected WoodType(String p_i225775_1_) {
      this.name = p_i225775_1_;
   }

   private static WoodType register(WoodType p_227047_0_) {
      VALUES.add(p_227047_0_);
      return p_227047_0_;
   }

   public static Stream<WoodType> values() {
      return VALUES.stream();
   }

   public String name() {
      return this.name;
   }
}
