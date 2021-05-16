package net.minecraft.stats;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StatisticsManager {
   protected final Object2IntMap<Stat<?>> stats = Object2IntMaps.synchronize(new Object2IntOpenHashMap<>());

   public StatisticsManager() {
      this.stats.defaultReturnValue(0);
   }

   public void increment(PlayerEntity p_150871_1_, Stat<?> p_150871_2_, int p_150871_3_) {
      int i = (int)Math.min((long)this.getValue(p_150871_2_) + (long)p_150871_3_, 2147483647L);
      this.setValue(p_150871_1_, p_150871_2_, i);
   }

   public void setValue(PlayerEntity p_150873_1_, Stat<?> p_150873_2_, int p_150873_3_) {
      this.stats.put(p_150873_2_, p_150873_3_);
   }

   @OnlyIn(Dist.CLIENT)
   public <T> int getValue(StatType<T> p_199060_1_, T p_199060_2_) {
      return p_199060_1_.contains(p_199060_2_) ? this.getValue(p_199060_1_.get(p_199060_2_)) : 0;
   }

   public int getValue(Stat<?> p_77444_1_) {
      return this.stats.getInt(p_77444_1_);
   }
}
