package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightTexture implements AutoCloseable {
   private final DynamicTexture lightTexture;
   private final NativeImage lightPixels;
   private final ResourceLocation lightTextureLocation;
   private boolean updateLightTexture;
   private float blockLightRedFlicker;
   private final GameRenderer renderer;
   private final Minecraft minecraft;

   public LightTexture(GameRenderer p_i225968_1_, Minecraft p_i225968_2_) {
      this.renderer = p_i225968_1_;
      this.minecraft = p_i225968_2_;
      this.lightTexture = new DynamicTexture(16, 16, false);
      this.lightTextureLocation = this.minecraft.getTextureManager().register("light_map", this.lightTexture);
      this.lightPixels = this.lightTexture.getPixels();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            this.lightPixels.setPixelRGBA(j, i, -1);
         }
      }

      this.lightTexture.upload();
   }

   public void close() {
      this.lightTexture.close();
   }

   public void tick() {
      this.blockLightRedFlicker = (float)((double)this.blockLightRedFlicker + (Math.random() - Math.random()) * Math.random() * Math.random() * 0.1D);
      this.blockLightRedFlicker = (float)((double)this.blockLightRedFlicker * 0.9D);
      this.updateLightTexture = true;
   }

   public void turnOffLightLayer() {
      RenderSystem.activeTexture(33986);
      RenderSystem.disableTexture();
      RenderSystem.activeTexture(33984);
   }

   public void turnOnLightLayer() {
      RenderSystem.activeTexture(33986);
      RenderSystem.matrixMode(5890);
      RenderSystem.loadIdentity();
      float f = 0.00390625F;
      RenderSystem.scalef(0.00390625F, 0.00390625F, 0.00390625F);
      RenderSystem.translatef(8.0F, 8.0F, 8.0F);
      RenderSystem.matrixMode(5888);
      this.minecraft.getTextureManager().bind(this.lightTextureLocation);
      RenderSystem.texParameter(3553, 10241, 9729);
      RenderSystem.texParameter(3553, 10240, 9729);
      RenderSystem.texParameter(3553, 10242, 10496);
      RenderSystem.texParameter(3553, 10243, 10496);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableTexture();
      RenderSystem.activeTexture(33984);
   }

   public void updateLightTexture(float p_205106_1_) {
      if (this.updateLightTexture) {
         this.updateLightTexture = false;
         this.minecraft.getProfiler().push("lightTex");
         ClientWorld clientworld = this.minecraft.level;
         if (clientworld != null) {
            float f = clientworld.getSkyDarken(1.0F);
            float f1;
            if (clientworld.getSkyFlashTime() > 0) {
               f1 = 1.0F;
            } else {
               f1 = f * 0.95F + 0.05F;
            }

            float f3 = this.minecraft.player.getWaterVision();
            float f2;
            if (this.minecraft.player.hasEffect(Effects.NIGHT_VISION)) {
               f2 = GameRenderer.getNightVisionScale(this.minecraft.player, p_205106_1_);
            } else if (f3 > 0.0F && this.minecraft.player.hasEffect(Effects.CONDUIT_POWER)) {
               f2 = f3;
            } else {
               f2 = 0.0F;
            }

            Vector3f vector3f = new Vector3f(f, f, 1.0F);
            vector3f.lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
            float f4 = this.blockLightRedFlicker + 1.5F;
            Vector3f vector3f1 = new Vector3f();

            for(int i = 0; i < 16; ++i) {
               for(int j = 0; j < 16; ++j) {
                  float f5 = this.getBrightness(clientworld, i) * f1;
                  float f6 = this.getBrightness(clientworld, j) * f4;
                  float f7 = f6 * ((f6 * 0.6F + 0.4F) * 0.6F + 0.4F);
                  float f8 = f6 * (f6 * f6 * 0.6F + 0.4F);
                  vector3f1.set(f6, f7, f8);
                  if (clientworld.effects().forceBrightLightmap()) {
                     vector3f1.lerp(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
                  } else {
                     Vector3f vector3f2 = vector3f.copy();
                     vector3f2.mul(f5);
                     vector3f1.add(vector3f2);
                     vector3f1.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                     if (this.renderer.getDarkenWorldAmount(p_205106_1_) > 0.0F) {
                        float f9 = this.renderer.getDarkenWorldAmount(p_205106_1_);
                        Vector3f vector3f3 = vector3f1.copy();
                        vector3f3.mul(0.7F, 0.6F, 0.6F);
                        vector3f1.lerp(vector3f3, f9);
                     }
                  }

                  vector3f1.clamp(0.0F, 1.0F);
                  if (f2 > 0.0F) {
                     float f10 = Math.max(vector3f1.x(), Math.max(vector3f1.y(), vector3f1.z()));
                     if (f10 < 1.0F) {
                        float f12 = 1.0F / f10;
                        Vector3f vector3f5 = vector3f1.copy();
                        vector3f5.mul(f12);
                        vector3f1.lerp(vector3f5, f2);
                     }
                  }

                  float f11 = (float)this.minecraft.options.gamma;
                  Vector3f vector3f4 = vector3f1.copy();
                  vector3f4.map(this::notGamma);
                  vector3f1.lerp(vector3f4, f11);
                  vector3f1.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                  vector3f1.clamp(0.0F, 1.0F);
                  vector3f1.mul(255.0F);
                  int j1 = 255;
                  int k = (int)vector3f1.x();
                  int l = (int)vector3f1.y();
                  int i1 = (int)vector3f1.z();
                  this.lightPixels.setPixelRGBA(j, i, -16777216 | i1 << 16 | l << 8 | k);
               }
            }

            this.lightTexture.upload();
            this.minecraft.getProfiler().pop();
         }
      }
   }

   private float notGamma(float p_228453_1_) {
      float f = 1.0F - p_228453_1_;
      return 1.0F - f * f * f * f;
   }

   private float getBrightness(World p_228452_1_, int p_228452_2_) {
      return p_228452_1_.dimensionType().brightness(p_228452_2_);
   }

   public static int pack(int p_228451_0_, int p_228451_1_) {
      return p_228451_0_ << 4 | p_228451_1_ << 20;
   }

   public static int block(int p_228450_0_) {
      return (p_228450_0_ & 0xFFFF) >> 4; // Forge: Fix fullbright quads showing dark artifacts. Reported as MC-169806
   }

   public static int sky(int p_228454_0_) {
      return p_228454_0_ >> 20 & '\uffff';
   }
}
