package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CooldownTracker {
   private final Map<Item, CooldownTracker.Cooldown> cooldowns = Maps.newHashMap();
   private int tickCount;

   public boolean isOnCooldown(Item p_185141_1_) {
      return this.getCooldownPercent(p_185141_1_, 0.0F) > 0.0F;
   }

   public float getCooldownPercent(Item p_185143_1_, float p_185143_2_) {
      CooldownTracker.Cooldown cooldowntracker$cooldown = this.cooldowns.get(p_185143_1_);
      if (cooldowntracker$cooldown != null) {
         float f = (float)(cooldowntracker$cooldown.endTime - cooldowntracker$cooldown.startTime);
         float f1 = (float)cooldowntracker$cooldown.endTime - ((float)this.tickCount + p_185143_2_);
         return MathHelper.clamp(f1 / f, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   public void tick() {
      ++this.tickCount;
      if (!this.cooldowns.isEmpty()) {
         Iterator<Entry<Item, CooldownTracker.Cooldown>> iterator = this.cooldowns.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<Item, CooldownTracker.Cooldown> entry = iterator.next();
            if ((entry.getValue()).endTime <= this.tickCount) {
               iterator.remove();
               this.onCooldownEnded(entry.getKey());
            }
         }
      }

   }

   public void addCooldown(Item p_185145_1_, int p_185145_2_) {
      this.cooldowns.put(p_185145_1_, new CooldownTracker.Cooldown(this.tickCount, this.tickCount + p_185145_2_));
      this.onCooldownStarted(p_185145_1_, p_185145_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public void removeCooldown(Item p_185142_1_) {
      this.cooldowns.remove(p_185142_1_);
      this.onCooldownEnded(p_185142_1_);
   }

   protected void onCooldownStarted(Item p_185140_1_, int p_185140_2_) {
   }

   protected void onCooldownEnded(Item p_185146_1_) {
   }

   class Cooldown {
      private final int startTime;
      private final int endTime;

      private Cooldown(int p_i47037_2_, int p_i47037_3_) {
         this.startTime = p_i47037_2_;
         this.endTime = p_i47037_3_;
      }
   }
}
