package net.minecraft.world.gen.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.function.LongFunction;
import net.minecraft.util.Util;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public class LayerUtil {
   private static final Int2IntMap CATEGORIES = Util.make(new Int2IntOpenHashMap(), (p_242938_0_) -> {
      register(p_242938_0_, LayerUtil.Type.BEACH, 16);
      register(p_242938_0_, LayerUtil.Type.BEACH, 26);
      register(p_242938_0_, LayerUtil.Type.DESERT, 2);
      register(p_242938_0_, LayerUtil.Type.DESERT, 17);
      register(p_242938_0_, LayerUtil.Type.DESERT, 130);
      register(p_242938_0_, LayerUtil.Type.EXTREME_HILLS, 131);
      register(p_242938_0_, LayerUtil.Type.EXTREME_HILLS, 162);
      register(p_242938_0_, LayerUtil.Type.EXTREME_HILLS, 20);
      register(p_242938_0_, LayerUtil.Type.EXTREME_HILLS, 3);
      register(p_242938_0_, LayerUtil.Type.EXTREME_HILLS, 34);
      register(p_242938_0_, LayerUtil.Type.FOREST, 27);
      register(p_242938_0_, LayerUtil.Type.FOREST, 28);
      register(p_242938_0_, LayerUtil.Type.FOREST, 29);
      register(p_242938_0_, LayerUtil.Type.FOREST, 157);
      register(p_242938_0_, LayerUtil.Type.FOREST, 132);
      register(p_242938_0_, LayerUtil.Type.FOREST, 4);
      register(p_242938_0_, LayerUtil.Type.FOREST, 155);
      register(p_242938_0_, LayerUtil.Type.FOREST, 156);
      register(p_242938_0_, LayerUtil.Type.FOREST, 18);
      register(p_242938_0_, LayerUtil.Type.ICY, 140);
      register(p_242938_0_, LayerUtil.Type.ICY, 13);
      register(p_242938_0_, LayerUtil.Type.ICY, 12);
      register(p_242938_0_, LayerUtil.Type.JUNGLE, 168);
      register(p_242938_0_, LayerUtil.Type.JUNGLE, 169);
      register(p_242938_0_, LayerUtil.Type.JUNGLE, 21);
      register(p_242938_0_, LayerUtil.Type.JUNGLE, 23);
      register(p_242938_0_, LayerUtil.Type.JUNGLE, 22);
      register(p_242938_0_, LayerUtil.Type.JUNGLE, 149);
      register(p_242938_0_, LayerUtil.Type.JUNGLE, 151);
      register(p_242938_0_, LayerUtil.Type.MESA, 37);
      register(p_242938_0_, LayerUtil.Type.MESA, 165);
      register(p_242938_0_, LayerUtil.Type.MESA, 167);
      register(p_242938_0_, LayerUtil.Type.MESA, 166);
      register(p_242938_0_, LayerUtil.Type.BADLANDS_PLATEAU, 39);
      register(p_242938_0_, LayerUtil.Type.BADLANDS_PLATEAU, 38);
      register(p_242938_0_, LayerUtil.Type.MUSHROOM, 14);
      register(p_242938_0_, LayerUtil.Type.MUSHROOM, 15);
      register(p_242938_0_, LayerUtil.Type.NONE, 25);
      register(p_242938_0_, LayerUtil.Type.OCEAN, 46);
      register(p_242938_0_, LayerUtil.Type.OCEAN, 49);
      register(p_242938_0_, LayerUtil.Type.OCEAN, 50);
      register(p_242938_0_, LayerUtil.Type.OCEAN, 48);
      register(p_242938_0_, LayerUtil.Type.OCEAN, 24);
      register(p_242938_0_, LayerUtil.Type.OCEAN, 47);
      register(p_242938_0_, LayerUtil.Type.OCEAN, 10);
      register(p_242938_0_, LayerUtil.Type.OCEAN, 45);
      register(p_242938_0_, LayerUtil.Type.OCEAN, 0);
      register(p_242938_0_, LayerUtil.Type.OCEAN, 44);
      register(p_242938_0_, LayerUtil.Type.PLAINS, 1);
      register(p_242938_0_, LayerUtil.Type.PLAINS, 129);
      register(p_242938_0_, LayerUtil.Type.RIVER, 11);
      register(p_242938_0_, LayerUtil.Type.RIVER, 7);
      register(p_242938_0_, LayerUtil.Type.SAVANNA, 35);
      register(p_242938_0_, LayerUtil.Type.SAVANNA, 36);
      register(p_242938_0_, LayerUtil.Type.SAVANNA, 163);
      register(p_242938_0_, LayerUtil.Type.SAVANNA, 164);
      register(p_242938_0_, LayerUtil.Type.SWAMP, 6);
      register(p_242938_0_, LayerUtil.Type.SWAMP, 134);
      register(p_242938_0_, LayerUtil.Type.TAIGA, 160);
      register(p_242938_0_, LayerUtil.Type.TAIGA, 161);
      register(p_242938_0_, LayerUtil.Type.TAIGA, 32);
      register(p_242938_0_, LayerUtil.Type.TAIGA, 33);
      register(p_242938_0_, LayerUtil.Type.TAIGA, 30);
      register(p_242938_0_, LayerUtil.Type.TAIGA, 31);
      register(p_242938_0_, LayerUtil.Type.TAIGA, 158);
      register(p_242938_0_, LayerUtil.Type.TAIGA, 5);
      register(p_242938_0_, LayerUtil.Type.TAIGA, 19);
      register(p_242938_0_, LayerUtil.Type.TAIGA, 133);
   });

   public static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> zoom(long p_202829_0_, IAreaTransformer1 p_202829_2_, IAreaFactory<T> p_202829_3_, int p_202829_4_, LongFunction<C> p_202829_5_) {
      IAreaFactory<T> iareafactory = p_202829_3_;

      for(int i = 0; i < p_202829_4_; ++i) {
         iareafactory = p_202829_2_.run(p_202829_5_.apply(p_202829_0_ + (long)i), iareafactory);
      }

      return iareafactory;
   }

   private static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> getDefaultLayer(boolean p_237216_0_, int p_237216_1_, int p_237216_2_, LongFunction<C> p_237216_3_) {
      IAreaFactory<T> iareafactory = IslandLayer.INSTANCE.run(p_237216_3_.apply(1L));
      iareafactory = ZoomLayer.FUZZY.run(p_237216_3_.apply(2000L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.run(p_237216_3_.apply(1L), iareafactory);
      iareafactory = ZoomLayer.NORMAL.run(p_237216_3_.apply(2001L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.run(p_237216_3_.apply(2L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.run(p_237216_3_.apply(50L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.run(p_237216_3_.apply(70L), iareafactory);
      iareafactory = RemoveTooMuchOceanLayer.INSTANCE.run(p_237216_3_.apply(2L), iareafactory);
      IAreaFactory<T> iareafactory1 = OceanLayer.INSTANCE.run(p_237216_3_.apply(2L));
      iareafactory1 = zoom(2001L, ZoomLayer.NORMAL, iareafactory1, 6, p_237216_3_);
      iareafactory = AddSnowLayer.INSTANCE.run(p_237216_3_.apply(2L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.run(p_237216_3_.apply(3L), iareafactory);
      iareafactory = EdgeLayer.CoolWarm.INSTANCE.run(p_237216_3_.apply(2L), iareafactory);
      iareafactory = EdgeLayer.HeatIce.INSTANCE.run(p_237216_3_.apply(2L), iareafactory);
      iareafactory = EdgeLayer.Special.INSTANCE.run(p_237216_3_.apply(3L), iareafactory);
      iareafactory = ZoomLayer.NORMAL.run(p_237216_3_.apply(2002L), iareafactory);
      iareafactory = ZoomLayer.NORMAL.run(p_237216_3_.apply(2003L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.run(p_237216_3_.apply(4L), iareafactory);
      iareafactory = AddMushroomIslandLayer.INSTANCE.run(p_237216_3_.apply(5L), iareafactory);
      iareafactory = DeepOceanLayer.INSTANCE.run(p_237216_3_.apply(4L), iareafactory);
      iareafactory = zoom(1000L, ZoomLayer.NORMAL, iareafactory, 0, p_237216_3_);
      IAreaFactory<T> lvt_6_1_ = zoom(1000L, ZoomLayer.NORMAL, iareafactory, 0, p_237216_3_);
      lvt_6_1_ = StartRiverLayer.INSTANCE.run(p_237216_3_.apply(100L), lvt_6_1_);
      IAreaFactory<T> lvt_7_1_ = (new BiomeLayer(p_237216_0_)).run(p_237216_3_.apply(200L), iareafactory);
      lvt_7_1_ = AddBambooForestLayer.INSTANCE.run(p_237216_3_.apply(1001L), lvt_7_1_);
      lvt_7_1_ = zoom(1000L, ZoomLayer.NORMAL, lvt_7_1_, 2, p_237216_3_);
      lvt_7_1_ = EdgeBiomeLayer.INSTANCE.run(p_237216_3_.apply(1000L), lvt_7_1_);
      IAreaFactory<T> lvt_8_1_ = zoom(1000L, ZoomLayer.NORMAL, lvt_6_1_, 2, p_237216_3_);
      lvt_7_1_ = HillsLayer.INSTANCE.run(p_237216_3_.apply(1000L), lvt_7_1_, lvt_8_1_);
      lvt_6_1_ = zoom(1000L, ZoomLayer.NORMAL, lvt_6_1_, 2, p_237216_3_);
      lvt_6_1_ = zoom(1000L, ZoomLayer.NORMAL, lvt_6_1_, p_237216_2_, p_237216_3_);
      lvt_6_1_ = RiverLayer.INSTANCE.run(p_237216_3_.apply(1L), lvt_6_1_);
      lvt_6_1_ = SmoothLayer.INSTANCE.run(p_237216_3_.apply(1000L), lvt_6_1_);
      lvt_7_1_ = RareBiomeLayer.INSTANCE.run(p_237216_3_.apply(1001L), lvt_7_1_);

      for(int i = 0; i < p_237216_1_; ++i) {
         lvt_7_1_ = ZoomLayer.NORMAL.run(p_237216_3_.apply((long)(1000 + i)), lvt_7_1_);
         if (i == 0) {
            lvt_7_1_ = AddIslandLayer.INSTANCE.run(p_237216_3_.apply(3L), lvt_7_1_);
         }

         if (i == 1 || p_237216_1_ == 1) {
            lvt_7_1_ = ShoreLayer.INSTANCE.run(p_237216_3_.apply(1000L), lvt_7_1_);
         }
      }

      lvt_7_1_ = SmoothLayer.INSTANCE.run(p_237216_3_.apply(1000L), lvt_7_1_);
      lvt_7_1_ = MixRiverLayer.INSTANCE.run(p_237216_3_.apply(100L), lvt_7_1_, lvt_6_1_);
      return MixOceansLayer.INSTANCE.run(p_237216_3_.apply(100L), lvt_7_1_, iareafactory1);
   }

   public static Layer getDefaultLayer(long p_237215_0_, boolean p_237215_2_, int p_237215_3_, int p_237215_4_) {
      int i = 25;
      IAreaFactory<LazyArea> iareafactory = getDefaultLayer(p_237215_2_, p_237215_3_, p_237215_4_, (p_227473_2_) -> {
         return new LazyAreaLayerContext(25, p_237215_0_, p_227473_2_);
      });
      return new Layer(iareafactory);
   }

   public static boolean isSame(int p_202826_0_, int p_202826_1_) {
      if (p_202826_0_ == p_202826_1_) {
         return true;
      } else {
         return CATEGORIES.get(p_202826_0_) == CATEGORIES.get(p_202826_1_);
      }
   }

   private static void register(Int2IntOpenHashMap p_242939_0_, LayerUtil.Type p_242939_1_, int p_242939_2_) {
      p_242939_0_.put(p_242939_2_, p_242939_1_.ordinal());
   }

   protected static boolean isOcean(int p_202827_0_) {
      return p_202827_0_ == 44 || p_202827_0_ == 45 || p_202827_0_ == 0 || p_202827_0_ == 46 || p_202827_0_ == 10 || p_202827_0_ == 47 || p_202827_0_ == 48 || p_202827_0_ == 24 || p_202827_0_ == 49 || p_202827_0_ == 50;
   }

   protected static boolean isShallowOcean(int p_203631_0_) {
      return p_203631_0_ == 44 || p_203631_0_ == 45 || p_203631_0_ == 0 || p_203631_0_ == 46 || p_203631_0_ == 10;
   }

   static enum Type {
      NONE,
      TAIGA,
      EXTREME_HILLS,
      JUNGLE,
      MESA,
      BADLANDS_PLATEAU,
      PLAINS,
      SAVANNA,
      ICY,
      BEACH,
      FOREST,
      OCEAN,
      DESERT,
      RIVER,
      SWAMP,
      MUSHROOM;
   }
}
