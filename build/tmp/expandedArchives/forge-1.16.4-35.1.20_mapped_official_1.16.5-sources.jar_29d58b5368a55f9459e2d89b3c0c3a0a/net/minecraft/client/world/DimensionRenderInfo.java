package net.minecraft.client.world;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class DimensionRenderInfo {
   private static final Object2ObjectMap<ResourceLocation, DimensionRenderInfo> EFFECTS = Util.make(new Object2ObjectArrayMap<>(), (p_239214_0_) -> {
      DimensionRenderInfo.Overworld dimensionrenderinfo$overworld = new DimensionRenderInfo.Overworld();
      p_239214_0_.defaultReturnValue(dimensionrenderinfo$overworld);
      p_239214_0_.put(DimensionType.OVERWORLD_EFFECTS, dimensionrenderinfo$overworld);
      p_239214_0_.put(DimensionType.NETHER_EFFECTS, new DimensionRenderInfo.Nether());
      p_239214_0_.put(DimensionType.END_EFFECTS, new DimensionRenderInfo.End());
   });
   private final float[] sunriseCol = new float[4];
   private final float cloudLevel;
   private final boolean hasGround;
   private final DimensionRenderInfo.FogType skyType;
   private final boolean forceBrightLightmap;
   private final boolean constantAmbientLight;
   private net.minecraftforge.client.IWeatherRenderHandler weatherRenderHandler = null;
   private net.minecraftforge.client.ISkyRenderHandler skyRenderHandler = null;
   private net.minecraftforge.client.ICloudRenderHandler cloudRenderHandler = null;

   public DimensionRenderInfo(float p_i241259_1_, boolean p_i241259_2_, DimensionRenderInfo.FogType p_i241259_3_, boolean p_i241259_4_, boolean p_i241259_5_) {
      this.cloudLevel = p_i241259_1_;
      this.hasGround = p_i241259_2_;
      this.skyType = p_i241259_3_;
      this.forceBrightLightmap = p_i241259_4_;
      this.constantAmbientLight = p_i241259_5_;
   }

   public static DimensionRenderInfo forType(DimensionType p_243495_0_) {
      return EFFECTS.get(p_243495_0_.effectsLocation());
   }

   @Nullable
   public float[] getSunriseColor(float p_230492_1_, float p_230492_2_) {
      float f = 0.4F;
      float f1 = MathHelper.cos(p_230492_1_ * ((float)Math.PI * 2F)) - 0.0F;
      float f2 = -0.0F;
      if (f1 >= -0.4F && f1 <= 0.4F) {
         float f3 = (f1 - -0.0F) / 0.4F * 0.5F + 0.5F;
         float f4 = 1.0F - (1.0F - MathHelper.sin(f3 * (float)Math.PI)) * 0.99F;
         f4 = f4 * f4;
         this.sunriseCol[0] = f3 * 0.3F + 0.7F;
         this.sunriseCol[1] = f3 * f3 * 0.7F + 0.2F;
         this.sunriseCol[2] = f3 * f3 * 0.0F + 0.2F;
         this.sunriseCol[3] = f4;
         return this.sunriseCol;
      } else {
         return null;
      }
   }

   public float getCloudHeight() {
      return this.cloudLevel;
   }

   public boolean hasGround() {
      return this.hasGround;
   }

   public abstract Vector3d getBrightnessDependentFogColor(Vector3d p_230494_1_, float p_230494_2_);

   public abstract boolean isFoggyAt(int p_230493_1_, int p_230493_2_);

   public DimensionRenderInfo.FogType skyType() {
      return this.skyType;
   }

   public boolean forceBrightLightmap() {
      return this.forceBrightLightmap;
   }

   public boolean constantAmbientLight() {
      return this.constantAmbientLight;
   }

   public void setWeatherRenderHandler(net.minecraftforge.client.IWeatherRenderHandler weatherRenderHandler) {
      this.weatherRenderHandler = weatherRenderHandler;
   }
   public void setSkyRenderHandler(net.minecraftforge.client.ISkyRenderHandler skyRenderHandler) {
      this.skyRenderHandler = skyRenderHandler;
   }
   public void setCloudRenderHandler(net.minecraftforge.client.ICloudRenderHandler cloudRenderHandler) {
      this.cloudRenderHandler = cloudRenderHandler;
   }
   @Nullable
   public net.minecraftforge.client.ICloudRenderHandler getCloudRenderHandler() {
      return cloudRenderHandler;
   }
   @Nullable
   public net.minecraftforge.client.IWeatherRenderHandler getWeatherRenderHandler() {
      return weatherRenderHandler;
   }
   @Nullable
   public net.minecraftforge.client.ISkyRenderHandler getSkyRenderHandler() {
      return skyRenderHandler;
   }

   @OnlyIn(Dist.CLIENT)
   public static class End extends DimensionRenderInfo {
      public End() {
         super(Float.NaN, false, DimensionRenderInfo.FogType.END, true, false);
      }

      public Vector3d getBrightnessDependentFogColor(Vector3d p_230494_1_, float p_230494_2_) {
         return p_230494_1_.scale((double)0.15F);
      }

      public boolean isFoggyAt(int p_230493_1_, int p_230493_2_) {
         return false;
      }

      @Nullable
      public float[] getSunriseColor(float p_230492_1_, float p_230492_2_) {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum FogType {
      NONE,
      NORMAL,
      END;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Nether extends DimensionRenderInfo {
      public Nether() {
         super(Float.NaN, true, DimensionRenderInfo.FogType.NONE, false, true);
      }

      public Vector3d getBrightnessDependentFogColor(Vector3d p_230494_1_, float p_230494_2_) {
         return p_230494_1_;
      }

      public boolean isFoggyAt(int p_230493_1_, int p_230493_2_) {
         return true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Overworld extends DimensionRenderInfo {
      public Overworld() {
         super(128.0F, true, DimensionRenderInfo.FogType.NORMAL, false, false);
      }

      public Vector3d getBrightnessDependentFogColor(Vector3d p_230494_1_, float p_230494_2_) {
         return p_230494_1_.multiply((double)(p_230494_2_ * 0.94F + 0.06F), (double)(p_230494_2_ * 0.94F + 0.06F), (double)(p_230494_2_ * 0.91F + 0.09F));
      }

      public boolean isFoggyAt(int p_230493_1_, int p_230493_2_) {
         return false;
      }
   }
}
