package net.minecraft.client.audio;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BackgroundMusicSelector {
   public static final Codec<BackgroundMusicSelector> CODEC = RecordCodecBuilder.create((p_232663_0_) -> {
      return p_232663_0_.group(SoundEvent.CODEC.fieldOf("sound").forGetter((p_232669_0_) -> {
         return p_232669_0_.event;
      }), Codec.INT.fieldOf("min_delay").forGetter((p_232667_0_) -> {
         return p_232667_0_.minDelay;
      }), Codec.INT.fieldOf("max_delay").forGetter((p_232665_0_) -> {
         return p_232665_0_.maxDelay;
      }), Codec.BOOL.fieldOf("replace_current_music").forGetter((p_232662_0_) -> {
         return p_232662_0_.replaceCurrentMusic;
      })).apply(p_232663_0_, BackgroundMusicSelector::new);
   });
   private final SoundEvent event;
   private final int minDelay;
   private final int maxDelay;
   private final boolean replaceCurrentMusic;

   public BackgroundMusicSelector(SoundEvent p_i231428_1_, int p_i231428_2_, int p_i231428_3_, boolean p_i231428_4_) {
      this.event = p_i231428_1_;
      this.minDelay = p_i231428_2_;
      this.maxDelay = p_i231428_3_;
      this.replaceCurrentMusic = p_i231428_4_;
   }

   @OnlyIn(Dist.CLIENT)
   public SoundEvent getEvent() {
      return this.event;
   }

   @OnlyIn(Dist.CLIENT)
   public int getMinDelay() {
      return this.minDelay;
   }

   @OnlyIn(Dist.CLIENT)
   public int getMaxDelay() {
      return this.maxDelay;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean replaceCurrentMusic() {
      return this.replaceCurrentMusic;
   }
}
