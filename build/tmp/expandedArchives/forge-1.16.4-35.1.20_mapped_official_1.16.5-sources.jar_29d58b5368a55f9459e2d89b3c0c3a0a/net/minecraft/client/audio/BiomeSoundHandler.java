package net.minecraft.client.audio;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Optional;
import java.util.Random;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.MoodSoundAmbience;
import net.minecraft.world.biome.SoundAdditionsAmbience;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeSoundHandler implements IAmbientSoundHandler {
   private final ClientPlayerEntity player;
   private final SoundHandler soundManager;
   private final BiomeManager biomeManager;
   private final Random random;
   private Object2ObjectArrayMap<Biome, BiomeSoundHandler.Sound> loopSounds = new Object2ObjectArrayMap<>();
   private Optional<MoodSoundAmbience> moodSettings = Optional.empty();
   private Optional<SoundAdditionsAmbience> additionsSettings = Optional.empty();
   private float moodiness;
   private Biome previousBiome;

   public BiomeSoundHandler(ClientPlayerEntity p_i232488_1_, SoundHandler p_i232488_2_, BiomeManager p_i232488_3_) {
      this.random = p_i232488_1_.level.getRandom();
      this.player = p_i232488_1_;
      this.soundManager = p_i232488_2_;
      this.biomeManager = p_i232488_3_;
   }

   public float getMoodiness() {
      return this.moodiness;
   }

   public void tick() {
      this.loopSounds.values().removeIf(TickableSound::isStopped);
      Biome biome = this.biomeManager.getNoiseBiomeAtPosition(this.player.getX(), this.player.getY(), this.player.getZ());
      if (biome != this.previousBiome) {
         this.previousBiome = biome;
         this.moodSettings = biome.getAmbientMood();
         this.additionsSettings = biome.getAmbientAdditions();
         this.loopSounds.values().forEach(BiomeSoundHandler.Sound::fadeOut);
         biome.getAmbientLoop().ifPresent((p_239522_2_) -> {
            BiomeSoundHandler.Sound biomesoundhandler$sound = this.loopSounds.compute(biome, (p_239519_2_, p_239519_3_) -> {
               if (p_239519_3_ == null) {
                  p_239519_3_ = new BiomeSoundHandler.Sound(p_239522_2_);
                  this.soundManager.play(p_239519_3_);
               }

               p_239519_3_.fadeIn();
               return p_239519_3_;
            });
         });
      }

      this.additionsSettings.ifPresent((p_239520_1_) -> {
         if (this.random.nextDouble() < p_239520_1_.getTickChance()) {
            this.soundManager.play(SimpleSound.forAmbientAddition(p_239520_1_.getSoundEvent()));
         }

      });
      this.moodSettings.ifPresent((p_239521_1_) -> {
         World world = this.player.level;
         int i = p_239521_1_.getBlockSearchExtent() * 2 + 1;
         BlockPos blockpos = new BlockPos(this.player.getX() + (double)this.random.nextInt(i) - (double)p_239521_1_.getBlockSearchExtent(), this.player.getEyeY() + (double)this.random.nextInt(i) - (double)p_239521_1_.getBlockSearchExtent(), this.player.getZ() + (double)this.random.nextInt(i) - (double)p_239521_1_.getBlockSearchExtent());
         int j = world.getBrightness(LightType.SKY, blockpos);
         if (j > 0) {
            this.moodiness -= (float)j / (float)world.getMaxLightLevel() * 0.001F;
         } else {
            this.moodiness -= (float)(world.getBrightness(LightType.BLOCK, blockpos) - 1) / (float)p_239521_1_.getTickDelay();
         }

         if (this.moodiness >= 1.0F) {
            double d0 = (double)blockpos.getX() + 0.5D;
            double d1 = (double)blockpos.getY() + 0.5D;
            double d2 = (double)blockpos.getZ() + 0.5D;
            double d3 = d0 - this.player.getX();
            double d4 = d1 - this.player.getEyeY();
            double d5 = d2 - this.player.getZ();
            double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
            double d7 = d6 + p_239521_1_.getSoundPositionOffset();
            SimpleSound simplesound = SimpleSound.forAmbientMood(p_239521_1_.getSoundEvent(), this.player.getX() + d3 / d6 * d7, this.player.getEyeY() + d4 / d6 * d7, this.player.getZ() + d5 / d6 * d7);
            this.soundManager.play(simplesound);
            this.moodiness = 0.0F;
         } else {
            this.moodiness = Math.max(this.moodiness, 0.0F);
         }

      });
   }

   @OnlyIn(Dist.CLIENT)
   public static class Sound extends TickableSound {
      private int fadeDirection;
      private int fade;

      public Sound(SoundEvent p_i232489_1_) {
         super(p_i232489_1_, SoundCategory.AMBIENT);
         this.looping = true;
         this.delay = 0;
         this.volume = 1.0F;
         this.relative = true;
      }

      public void tick() {
         if (this.fade < 0) {
            this.stop();
         }

         this.fade += this.fadeDirection;
         this.volume = MathHelper.clamp((float)this.fade / 40.0F, 0.0F, 1.0F);
      }

      public void fadeOut() {
         this.fade = Math.min(this.fade, 40);
         this.fadeDirection = -1;
      }

      public void fadeIn() {
         this.fade = Math.max(0, this.fade);
         this.fadeDirection = 1;
      }
   }
}
