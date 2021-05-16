package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundEventAccessor implements ISoundEventAccessor<Sound> {
   private final List<ISoundEventAccessor<Sound>> list = Lists.newArrayList();
   private final Random random = new Random();
   private final ResourceLocation location;
   @Nullable
   private final ITextComponent subtitle;

   public SoundEventAccessor(ResourceLocation p_i46521_1_, @Nullable String p_i46521_2_) {
      this.location = p_i46521_1_;
      this.subtitle = p_i46521_2_ == null ? null : new TranslationTextComponent(p_i46521_2_);
   }

   public int getWeight() {
      int i = 0;

      for(ISoundEventAccessor<Sound> isoundeventaccessor : this.list) {
         i += isoundeventaccessor.getWeight();
      }

      return i;
   }

   public Sound getSound() {
      int i = this.getWeight();
      if (!this.list.isEmpty() && i != 0) {
         int j = this.random.nextInt(i);

         for(ISoundEventAccessor<Sound> isoundeventaccessor : this.list) {
            j -= isoundeventaccessor.getWeight();
            if (j < 0) {
               return isoundeventaccessor.getSound();
            }
         }

         return SoundHandler.EMPTY_SOUND;
      } else {
         return SoundHandler.EMPTY_SOUND;
      }
   }

   public void addSound(ISoundEventAccessor<Sound> p_188715_1_) {
      this.list.add(p_188715_1_);
   }

   @Nullable
   public ITextComponent getSubtitle() {
      return this.subtitle;
   }

   public void preloadIfRequired(SoundEngine p_217867_1_) {
      for(ISoundEventAccessor<Sound> isoundeventaccessor : this.list) {
         isoundeventaccessor.preloadIfRequired(p_217867_1_);
      }

   }
}
