package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FogRenderer {
   private static float fogRed;
   private static float fogGreen;
   private static float fogBlue;
   private static int targetBiomeFog = -1;
   private static int previousBiomeFog = -1;
   private static long biomeChangedTime = -1L;

   public static void setupColor(ActiveRenderInfo p_228371_0_, float p_228371_1_, ClientWorld p_228371_2_, int p_228371_3_, float p_228371_4_) {
      FluidState fluidstate = p_228371_0_.getFluidInCamera();
      if (fluidstate.is(FluidTags.WATER)) {
         long i = Util.getMillis();
         int j = p_228371_2_.getBiome(new BlockPos(p_228371_0_.getPosition())).getWaterFogColor();
         if (biomeChangedTime < 0L) {
            targetBiomeFog = j;
            previousBiomeFog = j;
            biomeChangedTime = i;
         }

         int k = targetBiomeFog >> 16 & 255;
         int l = targetBiomeFog >> 8 & 255;
         int i1 = targetBiomeFog & 255;
         int j1 = previousBiomeFog >> 16 & 255;
         int k1 = previousBiomeFog >> 8 & 255;
         int l1 = previousBiomeFog & 255;
         float f = MathHelper.clamp((float)(i - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
         float f1 = MathHelper.lerp(f, (float)j1, (float)k);
         float f2 = MathHelper.lerp(f, (float)k1, (float)l);
         float f3 = MathHelper.lerp(f, (float)l1, (float)i1);
         fogRed = f1 / 255.0F;
         fogGreen = f2 / 255.0F;
         fogBlue = f3 / 255.0F;
         if (targetBiomeFog != j) {
            targetBiomeFog = j;
            previousBiomeFog = MathHelper.floor(f1) << 16 | MathHelper.floor(f2) << 8 | MathHelper.floor(f3);
            biomeChangedTime = i;
         }
      } else if (fluidstate.is(FluidTags.LAVA)) {
         fogRed = 0.6F;
         fogGreen = 0.1F;
         fogBlue = 0.0F;
         biomeChangedTime = -1L;
      } else {
         float f4 = 0.25F + 0.75F * (float)p_228371_3_ / 32.0F;
         f4 = 1.0F - (float)Math.pow((double)f4, 0.25D);
         Vector3d vector3d = p_228371_2_.getSkyColor(p_228371_0_.getBlockPosition(), p_228371_1_);
         float f5 = (float)vector3d.x;
         float f8 = (float)vector3d.y;
         float f11 = (float)vector3d.z;
         float f12 = MathHelper.clamp(MathHelper.cos(p_228371_2_.getTimeOfDay(p_228371_1_) * ((float)Math.PI * 2F)) * 2.0F + 0.5F, 0.0F, 1.0F);
         BiomeManager biomemanager = p_228371_2_.getBiomeManager();
         Vector3d vector3d1 = p_228371_0_.getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
         Vector3d vector3d2 = CubicSampler.gaussianSampleVec3(vector3d1, (p_239218_3_, p_239218_4_, p_239218_5_) -> {
            return p_228371_2_.effects().getBrightnessDependentFogColor(Vector3d.fromRGB24(biomemanager.getNoiseBiomeAtQuart(p_239218_3_, p_239218_4_, p_239218_5_).getFogColor()), f12);
         });
         fogRed = (float)vector3d2.x();
         fogGreen = (float)vector3d2.y();
         fogBlue = (float)vector3d2.z();
         if (p_228371_3_ >= 4) {
            float f13 = MathHelper.sin(p_228371_2_.getSunAngle(p_228371_1_)) > 0.0F ? -1.0F : 1.0F;
            Vector3f vector3f = new Vector3f(f13, 0.0F, 0.0F);
            float f17 = p_228371_0_.getLookVector().dot(vector3f);
            if (f17 < 0.0F) {
               f17 = 0.0F;
            }

            if (f17 > 0.0F) {
               float[] afloat = p_228371_2_.effects().getSunriseColor(p_228371_2_.getTimeOfDay(p_228371_1_), p_228371_1_);
               if (afloat != null) {
                  f17 = f17 * afloat[3];
                  fogRed = fogRed * (1.0F - f17) + afloat[0] * f17;
                  fogGreen = fogGreen * (1.0F - f17) + afloat[1] * f17;
                  fogBlue = fogBlue * (1.0F - f17) + afloat[2] * f17;
               }
            }
         }

         fogRed += (f5 - fogRed) * f4;
         fogGreen += (f8 - fogGreen) * f4;
         fogBlue += (f11 - fogBlue) * f4;
         float f14 = p_228371_2_.getRainLevel(p_228371_1_);
         if (f14 > 0.0F) {
            float f15 = 1.0F - f14 * 0.5F;
            float f18 = 1.0F - f14 * 0.4F;
            fogRed *= f15;
            fogGreen *= f15;
            fogBlue *= f18;
         }

         float f16 = p_228371_2_.getThunderLevel(p_228371_1_);
         if (f16 > 0.0F) {
            float f19 = 1.0F - f16 * 0.5F;
            fogRed *= f19;
            fogGreen *= f19;
            fogBlue *= f19;
         }

         biomeChangedTime = -1L;
      }

      double d0 = p_228371_0_.getPosition().y * p_228371_2_.getLevelData().getClearColorScale();
      if (p_228371_0_.getEntity() instanceof LivingEntity && ((LivingEntity)p_228371_0_.getEntity()).hasEffect(Effects.BLINDNESS)) {
         int i2 = ((LivingEntity)p_228371_0_.getEntity()).getEffect(Effects.BLINDNESS).getDuration();
         if (i2 < 20) {
            d0 *= (double)(1.0F - (float)i2 / 20.0F);
         } else {
            d0 = 0.0D;
         }
      }

      if (d0 < 1.0D && !fluidstate.is(FluidTags.LAVA)) {
         if (d0 < 0.0D) {
            d0 = 0.0D;
         }

         d0 = d0 * d0;
         fogRed = (float)((double)fogRed * d0);
         fogGreen = (float)((double)fogGreen * d0);
         fogBlue = (float)((double)fogBlue * d0);
      }

      if (p_228371_4_ > 0.0F) {
         fogRed = fogRed * (1.0F - p_228371_4_) + fogRed * 0.7F * p_228371_4_;
         fogGreen = fogGreen * (1.0F - p_228371_4_) + fogGreen * 0.6F * p_228371_4_;
         fogBlue = fogBlue * (1.0F - p_228371_4_) + fogBlue * 0.6F * p_228371_4_;
      }

      if (fluidstate.is(FluidTags.WATER)) {
         float f6 = 0.0F;
         if (p_228371_0_.getEntity() instanceof ClientPlayerEntity) {
            ClientPlayerEntity clientplayerentity = (ClientPlayerEntity)p_228371_0_.getEntity();
            f6 = clientplayerentity.getWaterVision();
         }

         float f9 = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
         // Forge: fix MC-4647 and MC-10480
         if (Float.isInfinite(f9)) f9 = Math.nextAfter(f9, 0.0);
         fogRed = fogRed * (1.0F - f6) + fogRed * f9 * f6;
         fogGreen = fogGreen * (1.0F - f6) + fogGreen * f9 * f6;
         fogBlue = fogBlue * (1.0F - f6) + fogBlue * f9 * f6;
      } else if (p_228371_0_.getEntity() instanceof LivingEntity && ((LivingEntity)p_228371_0_.getEntity()).hasEffect(Effects.NIGHT_VISION)) {
         float f7 = GameRenderer.getNightVisionScale((LivingEntity)p_228371_0_.getEntity(), p_228371_1_);
         float f10 = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
         // Forge: fix MC-4647 and MC-10480
         if (Float.isInfinite(f10)) f10 = Math.nextAfter(f10, 0.0);
         fogRed = fogRed * (1.0F - f7) + fogRed * f10 * f7;
         fogGreen = fogGreen * (1.0F - f7) + fogGreen * f10 * f7;
         fogBlue = fogBlue * (1.0F - f7) + fogBlue * f10 * f7;
      }

      net.minecraftforge.client.event.EntityViewRenderEvent.FogColors event = new net.minecraftforge.client.event.EntityViewRenderEvent.FogColors(p_228371_0_, p_228371_1_, fogRed, fogGreen, fogBlue);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);

      fogRed = event.getRed();
      fogGreen = event.getGreen();
      fogBlue = event.getBlue();

      RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
   }

   public static void setupNoFog() {
      RenderSystem.fogDensity(0.0F);
      RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
   }
   @Deprecated // FORGE: Pass in partialTicks
   public static void setupFog(ActiveRenderInfo p_228372_0_, FogRenderer.FogType p_228372_1_, float p_228372_2_, boolean p_228372_3_) {
      setupFog(p_228372_0_, p_228372_1_, p_228372_2_, p_228372_3_, 0);
   }

   public static void setupFog(ActiveRenderInfo p_228372_0_, FogRenderer.FogType p_228372_1_, float p_228372_2_, boolean p_228372_3_, float partialTicks) {
      FluidState fluidstate = p_228372_0_.getFluidInCamera();
      Entity entity = p_228372_0_.getEntity();
      float hook = net.minecraftforge.client.ForgeHooksClient.getFogDensity(p_228372_1_, p_228372_0_, partialTicks, 0.1F);
      if (hook >= 0) RenderSystem.fogDensity(hook);
      else
      if (fluidstate.is(FluidTags.WATER)) {
         float f = 1.0F;
         f = 0.05F;
         if (entity instanceof ClientPlayerEntity) {
            ClientPlayerEntity clientplayerentity = (ClientPlayerEntity)entity;
            f -= clientplayerentity.getWaterVision() * clientplayerentity.getWaterVision() * 0.03F;
            Biome biome = clientplayerentity.level.getBiome(clientplayerentity.blockPosition());
            if (biome.getBiomeCategory() == Biome.Category.SWAMP) {
               f += 0.005F;
            }
         }

         RenderSystem.fogDensity(f);
         RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
      } else {
         float f2;
         float f3;
         if (fluidstate.is(FluidTags.LAVA)) {
            if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(Effects.FIRE_RESISTANCE)) {
               f2 = 0.0F;
               f3 = 3.0F;
            } else {
               f2 = 0.25F;
               f3 = 1.0F;
            }
         } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(Effects.BLINDNESS)) {
            int i = ((LivingEntity)entity).getEffect(Effects.BLINDNESS).getDuration();
            float f1 = MathHelper.lerp(Math.min(1.0F, (float)i / 20.0F), p_228372_2_, 5.0F);
            if (p_228372_1_ == FogRenderer.FogType.FOG_SKY) {
               f2 = 0.0F;
               f3 = f1 * 0.8F;
            } else {
               f2 = f1 * 0.25F;
               f3 = f1;
            }
         } else if (p_228372_3_) {
            f2 = p_228372_2_ * 0.05F;
            f3 = Math.min(p_228372_2_, 192.0F) * 0.5F;
         } else if (p_228372_1_ == FogRenderer.FogType.FOG_SKY) {
            f2 = 0.0F;
            f3 = p_228372_2_;
         } else {
            f2 = p_228372_2_ * 0.75F;
            f3 = p_228372_2_;
         }

         RenderSystem.fogStart(f2);
         RenderSystem.fogEnd(f3);
         RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
         RenderSystem.setupNvFogDistance();
         net.minecraftforge.client.ForgeHooksClient.onFogRender(p_228372_1_, p_228372_0_, partialTicks, f3);
      }

   }

   public static void levelFogColor() {
      RenderSystem.fog(2918, fogRed, fogGreen, fogBlue, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public static enum FogType {
      FOG_SKY,
      FOG_TERRAIN;
   }
}
