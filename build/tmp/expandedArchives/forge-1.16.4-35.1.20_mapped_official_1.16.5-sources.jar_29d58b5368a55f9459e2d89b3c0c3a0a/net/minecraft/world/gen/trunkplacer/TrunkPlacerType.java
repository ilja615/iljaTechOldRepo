package net.minecraft.world.gen.trunkplacer;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class TrunkPlacerType<P extends AbstractTrunkPlacer> {
   public static final TrunkPlacerType<StraightTrunkPlacer> STRAIGHT_TRUNK_PLACER = register("straight_trunk_placer", StraightTrunkPlacer.CODEC);
   public static final TrunkPlacerType<ForkyTrunkPlacer> FORKING_TRUNK_PLACER = register("forking_trunk_placer", ForkyTrunkPlacer.CODEC);
   public static final TrunkPlacerType<GiantTrunkPlacer> GIANT_TRUNK_PLACER = register("giant_trunk_placer", GiantTrunkPlacer.CODEC);
   public static final TrunkPlacerType<MegaJungleTrunkPlacer> MEGA_JUNGLE_TRUNK_PLACER = register("mega_jungle_trunk_placer", MegaJungleTrunkPlacer.CODEC);
   public static final TrunkPlacerType<DarkOakTrunkPlacer> DARK_OAK_TRUNK_PLACER = register("dark_oak_trunk_placer", DarkOakTrunkPlacer.CODEC);
   public static final TrunkPlacerType<FancyTrunkPlacer> FANCY_TRUNK_PLACER = register("fancy_trunk_placer", FancyTrunkPlacer.CODEC);
   private final Codec<P> codec;

   private static <P extends AbstractTrunkPlacer> TrunkPlacerType<P> register(String p_236928_0_, Codec<P> p_236928_1_) {
      return Registry.register(Registry.TRUNK_PLACER_TYPES, p_236928_0_, new TrunkPlacerType<>(p_236928_1_));
   }

   private TrunkPlacerType(Codec<P> p_i232061_1_) {
      this.codec = p_i232061_1_;
   }

   public Codec<P> codec() {
      return this.codec;
   }
}
