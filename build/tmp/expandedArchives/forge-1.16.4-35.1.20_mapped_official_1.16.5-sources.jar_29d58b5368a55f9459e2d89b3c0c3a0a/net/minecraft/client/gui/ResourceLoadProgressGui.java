package net.minecraft.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IAsyncReloader;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResourceLoadProgressGui extends LoadingGui {
   private static final ResourceLocation MOJANG_STUDIOS_LOGO_LOCATION = new ResourceLocation("textures/gui/title/mojangstudios.png");
   private static final int BRAND_BACKGROUND = ColorHelper.PackedColor.color(255, 239, 50, 61);
   private static final int BRAND_BACKGROUND_NO_ALPHA = BRAND_BACKGROUND & 16777215;
   private final Minecraft minecraft;
   private final IAsyncReloader reload;
   private final Consumer<Optional<Throwable>> onFinish;
   private final boolean fadeIn;
   private float currentProgress;
   private long fadeOutStart = -1L;
   private long fadeInStart = -1L;

   public ResourceLoadProgressGui(Minecraft p_i225928_1_, IAsyncReloader p_i225928_2_, Consumer<Optional<Throwable>> p_i225928_3_, boolean p_i225928_4_) {
      this.minecraft = p_i225928_1_;
      this.reload = p_i225928_2_;
      this.onFinish = p_i225928_3_;
      this.fadeIn = p_i225928_4_;
   }

   public static void registerTextures(Minecraft p_212970_0_) {
      p_212970_0_.getTextureManager().register(MOJANG_STUDIOS_LOGO_LOCATION, new ResourceLoadProgressGui.MojangLogoTexture());
   }

   public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
      int i = this.minecraft.getWindow().getGuiScaledWidth();
      int j = this.minecraft.getWindow().getGuiScaledHeight();
      long k = Util.getMillis();
      if (this.fadeIn && (this.reload.isApplying() || this.minecraft.screen != null) && this.fadeInStart == -1L) {
         this.fadeInStart = k;
      }

      float f = this.fadeOutStart > -1L ? (float)(k - this.fadeOutStart) / 1000.0F : -1.0F;
      float f1 = this.fadeInStart > -1L ? (float)(k - this.fadeInStart) / 500.0F : -1.0F;
      float f2;
      if (f >= 1.0F) {
         if (this.minecraft.screen != null) {
            this.minecraft.screen.render(p_230430_1_, 0, 0, p_230430_4_);
         }

         int l = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
         fill(p_230430_1_, 0, 0, i, j, BRAND_BACKGROUND_NO_ALPHA | l << 24);
         f2 = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
      } else if (this.fadeIn) {
         if (this.minecraft.screen != null && f1 < 1.0F) {
            this.minecraft.screen.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
         }

         int i2 = MathHelper.ceil(MathHelper.clamp((double)f1, 0.15D, 1.0D) * 255.0D);
         fill(p_230430_1_, 0, 0, i, j, BRAND_BACKGROUND_NO_ALPHA | i2 << 24);
         f2 = MathHelper.clamp(f1, 0.0F, 1.0F);
      } else {
         fill(p_230430_1_, 0, 0, i, j, BRAND_BACKGROUND);
         f2 = 1.0F;
      }

      int j2 = (int)((double)this.minecraft.getWindow().getGuiScaledWidth() * 0.5D);
      int i1 = (int)((double)this.minecraft.getWindow().getGuiScaledHeight() * 0.5D);
      double d0 = Math.min((double)this.minecraft.getWindow().getGuiScaledWidth() * 0.75D, (double)this.minecraft.getWindow().getGuiScaledHeight()) * 0.25D;
      int j1 = (int)(d0 * 0.5D);
      double d1 = d0 * 4.0D;
      int k1 = (int)(d1 * 0.5D);
      this.minecraft.getTextureManager().bind(MOJANG_STUDIOS_LOGO_LOCATION);
      RenderSystem.enableBlend();
      RenderSystem.blendEquation(32774);
      RenderSystem.blendFunc(770, 1);
      RenderSystem.alphaFunc(516, 0.0F);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, f2);
      blit(p_230430_1_, j2 - k1, i1 - j1, k1, (int)d0, -0.0625F, 0.0F, 120, 60, 120, 120);
      blit(p_230430_1_, j2, i1 - j1, k1, (int)d0, 0.0625F, 60.0F, 120, 60, 120, 120);
      RenderSystem.defaultBlendFunc();
      RenderSystem.defaultAlphaFunc();
      RenderSystem.disableBlend();
      int l1 = (int)((double)this.minecraft.getWindow().getGuiScaledHeight() * 0.8325D);
      float f3 = this.reload.getActualProgress();
      this.currentProgress = MathHelper.clamp(this.currentProgress * 0.95F + f3 * 0.050000012F, 0.0F, 1.0F);
      net.minecraftforge.fml.client.ClientModLoader.renderProgressText();
      if (f < 1.0F) {
         this.drawProgressBar(p_230430_1_, i / 2 - k1, l1 - 5, i / 2 + k1, l1 + 5, 1.0F - MathHelper.clamp(f, 0.0F, 1.0F));
      }

      if (f >= 2.0F) {
         this.minecraft.setOverlay((LoadingGui)null);
      }

      if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || f1 >= 2.0F)) {
         this.fadeOutStart = Util.getMillis(); // Moved up to guard against inf loops caused by callback
         try {
            this.reload.checkExceptions();
            this.onFinish.accept(Optional.empty());
         } catch (Throwable throwable) {
            this.onFinish.accept(Optional.of(throwable));
         }

         if (this.minecraft.screen != null) {
            this.minecraft.screen.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
         }
      }

   }

   private void drawProgressBar(MatrixStack p_238629_1_, int p_238629_2_, int p_238629_3_, int p_238629_4_, int p_238629_5_, float p_238629_6_) {
      int i = MathHelper.ceil((float)(p_238629_4_ - p_238629_2_ - 2) * this.currentProgress);
      int j = Math.round(p_238629_6_ * 255.0F);
      int k = ColorHelper.PackedColor.color(j, 255, 255, 255);
      fill(p_238629_1_, p_238629_2_ + 1, p_238629_3_, p_238629_4_ - 1, p_238629_3_ + 1, k);
      fill(p_238629_1_, p_238629_2_ + 1, p_238629_5_, p_238629_4_ - 1, p_238629_5_ - 1, k);
      fill(p_238629_1_, p_238629_2_, p_238629_3_, p_238629_2_ + 1, p_238629_5_, k);
      fill(p_238629_1_, p_238629_4_, p_238629_3_, p_238629_4_ - 1, p_238629_5_, k);
      fill(p_238629_1_, p_238629_2_ + 2, p_238629_3_ + 2, p_238629_2_ + i, p_238629_5_ - 2, k);
   }

   public boolean isPauseScreen() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   static class MojangLogoTexture extends SimpleTexture {
      public MojangLogoTexture() {
         super(ResourceLoadProgressGui.MOJANG_STUDIOS_LOGO_LOCATION);
      }

      protected SimpleTexture.TextureData getTextureImage(IResourceManager p_215246_1_) {
         Minecraft minecraft = Minecraft.getInstance();
         VanillaPack vanillapack = minecraft.getClientPackSource().getVanillaPack();

         try (InputStream inputstream = vanillapack.getResource(ResourcePackType.CLIENT_RESOURCES, ResourceLoadProgressGui.MOJANG_STUDIOS_LOGO_LOCATION)) {
            return new SimpleTexture.TextureData(new TextureMetadataSection(true, true), NativeImage.read(inputstream));
         } catch (IOException ioexception) {
            return new SimpleTexture.TextureData(ioexception);
         }
      }
   }
}
